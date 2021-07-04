/*-
 * >===license-start
 * RemoteLight
 * ===
 * Copyright (C) 2019 - 2020 Lars O.
 * ===
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * <===license-end
 */

package de.lars.remotelightclient;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.tinylog.Logger;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatInspector;

import de.lars.remotelightclient.plugins.SwingPluginInterface;
import de.lars.remotelightclient.screencolor.ScreenColorManager;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.components.dialogs.NoFileAccessDialog;
import de.lars.remotelightclient.ui.components.frames.SplashFrame;
import de.lars.remotelightclient.ui.console.CustomOutputStream;
import de.lars.remotelightclient.ui.font.DefaultFonts;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.cmd.StartParameterHandler;
import de.lars.remotelightcore.notification.Notification;
import de.lars.remotelightcore.notification.NotificationType;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightcore.utils.DirectoryUtil;
import de.lars.remotelightcore.utils.ExceptionHandler;
import de.lars.remotelightplugins.PluginManager;
import de.lars.remotelightrestapi.RestAPI;
import fi.iki.elonen.NanoHTTPD;

public class Main {
	
	public final static String VERSION = RemoteLightCore.VERSION;
	public final static String SCOPE = "swing";
	private static String cachedCommitID;
	
	private static Main instance;
	private RemoteLightCore remoteLightCore;
	private Set<JFrame> frames;
	private MainFrame mainFrame;
	private PluginManager pluginManager;

	/**
	 * Entry point of the program.
	 * @param args	supports some start parameters (see {@link StartParameterHandler})
	 */
	public static void main(String[] args) {
		if(Arrays.stream(args).anyMatch(s -> s.equalsIgnoreCase("-tray") || s.equalsIgnoreCase("-t"))) {
			SplashFrame.disable = true;
		}
		// show version number on splash screen
		SplashFrame.showSplashScreen();
		// initialize application
		new Main(args, true);
	}
	
	public Main(String[] args, boolean uiMode) {
		instance = this;
		frames = new HashSet<JFrame>();
		CustomOutputStream.init();
		setupOSSupport();
		
		// register error handler in MainFrame
		ExceptionHandler.registerListener(MainFrame.onException);
		
		// check file access
		IOException fileAcessExcpt = checkFileAccess();
		if(fileAcessExcpt != null) {
			System.err.println("No File Access!");
			fileAcessExcpt.printStackTrace();
			NoFileAccessDialog.showDialog(fileAcessExcpt.getMessage());
		}
		
		remoteLightCore = new RemoteLightCore(args, !uiMode);
		// register shutdown hook
		remoteLightCore.registerShutdownHook();
		// set ScreenColorManager
		remoteLightCore.setScreenColorManager(new ScreenColorManager());
		
		Logger.info("Starting RemoteLightClient version " + VERSION);
		
		DefaultFonts.registerDefaultFonts(); // register default fonts
		
		new StartUp(RemoteLightCore.startParameter);
		
		if(uiMode)
			startMainFrame();
		
		initPluginManager();
	}
	
	
	/**
	 * Initialize the plugin manager and load plugins.
	 */
	private void initPluginManager() {
		File pluginDir = new File(DirectoryUtil.getDataStoragePath() + "plugins");
		pluginDir.mkdirs();
		
		// create a new plugin manager
		pluginManager = new PluginManager(pluginDir, remoteLightCore, new SwingPluginInterface(this));
		// add scope
		pluginManager.getScopes().add(SCOPE);
		// check if plugin system is enabled
		if(getSettingsManager().getSetting(SettingBoolean.class, "plugins.enable").get()) {
			// load all plugins
			pluginManager.loadPlugins();
		} else {
			Logger.info("Plugin system is disabled in settings. Plugins are not loaded!");
		}
	}
	
	
	/**
	 * Starts the UI
	 */
	public void startMainFrame() {
		// set look and feel
		boolean failed = !setLookAndFeel();
		if(failed) {
			try {
				UIManager.setLookAndFeel(new MetalLookAndFeel());
			} catch (UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}
		}
		Logger.info((failed ? "[FAILED] switch to standard LaF: " : "Selected Look and Feel: ") + UIManager.getLookAndFeel().getName());
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					// initialize and show the main frame
					mainFrame = new MainFrame();
					mainFrame.setVisible(!RemoteLightCore.startParameter.tray);
					
					if(RemoteLightCore.DEVBUILD) {
						FlatInspector.install("ctrl shift alt X");
					}
				} catch (Exception e) {
					ExceptionHandler.handle(e);
				}
			}
		});
	}
	
	/**
	 * Set the Look and Feel from the {@code ui.laf} setting.
	 * @return	false, if the operation fails
	 */
	public boolean setLookAndFeel() {
		SettingSelection sLaF = (SettingSelection) remoteLightCore.getSettingsManager().getSettingFromId("ui.laf");
		UiUtils.setThemingEnabled(true);
		
		try {
			
			if(sLaF == null || sLaF.getSelected().equalsIgnoreCase("System default")) {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				setCustomWindowDecorations(false);
				return true;
			}
			
			String lafName = sLaF.getSelected();
			
			for(LookAndFeelInfo info : Style.getLookAndFeelInfo()) {
				if(lafName.equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
				}
			}
			
			// check if current LaF is FlatLaf LaF
			if(UIManager.getLookAndFeel().getID().startsWith("FlatLaf")) {
				UiUtils.setThemingEnabled(false);
				boolean customWindow = FlatLaf.supportsNativeWindowDecorations()
						&& getSettingsManager().getSetting(SettingBoolean.class, "ui.windowdecorations").get()
						&& getSettingsManager().getSetting(SettingSelection.class, "ui.style").getSelected().equals("LookAndFeel");
				// enable FlatLaf custom window decorations
				UIManager.put( "TitlePane.unifiedBackground", true );
				setCustomWindowDecorations(customWindow);
			} else {
				setCustomWindowDecorations(false);
			}
			
			return true;
		} catch (InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Enable or disable FlatLaf custom window decorations. Works only if
	 * a FlatLaf Look and Feel is enabled.
	 * @param enabled	whether to enable or disable window decorations
	 */
	public void setCustomWindowDecorations(boolean enabled) {
		FlatLaf.setUseNativeWindowDecorations(enabled);
	}
	
	/**
	 * Enable some OS specific functionalities, like Apple menu bar name.
	 */
	public static void setupOSSupport() {
		// MacOS menu bar application name
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("apple.awt.application.name", "RemoteLight");
	}
	
	public static String getGitCommitID() {
		if(cachedCommitID != null)
			return cachedCommitID;
		String commitID = null;
		InputStream in = Main.class.getResourceAsStream("/git.properties");
		if(in != null) {
			Properties prop = new Properties();
			try {
				prop.load(in);
				commitID = prop.getProperty("git.commit.id.abbrev");
			} catch (IOException e) {}
			try {
				in.close();
			} catch (IOException e) {}
		}
		cachedCommitID = commitID;
		return commitID;
	}
	
	/**
	 * Check data file read and write access.
	 * @return	null if the file is readable and writable,
	 * 			the exception otherwise
	 */
	public static IOException checkFileAccess() {
		IOException stack = null;
		File dataFile = new File(DirectoryUtil.getDataStoragePath() + DirectoryUtil.FILE_STORAGE_NAME);
		dataFile.getParentFile().mkdirs();
		if(dataFile.getParentFile().exists()) {
			// create temp file to test directory access
			try {
				File tmp = new File(dataFile.getParent(), "test.tmp");
				if(tmp.exists()) {
					if(!tmp.delete())
						stack = new IOException("Could not delete temp file 'test.tmp'. No file access?");
				} else if(!tmp.exists()) {
					tmp.createNewFile();
					tmp.delete();
				}
			} catch (Exception e) {
				stack = new IOException("Failed to create temporary test file. No file access?", e);
			}
			// check if data file is accessible
			if(dataFile.exists()) {
				boolean writable = Files.isWritable(dataFile.toPath());
				boolean readable = Files.isReadable(dataFile.toPath());
				if(!writable || !readable) {
					String errMsg = "Data file '" + dataFile.getAbsoluteFile() + "' ";
					if(!writable && !readable)
						errMsg += "is not writable and not readable.";
					else if(!writable)
						errMsg += "is not writable.";
					else if(!readable)
						errMsg = "is not readable.";
					stack = new IOException(errMsg);
				}
			}
		}
		return stack;
	}
	
	
	public void startRestApi() {
		final int port = getSettingsManager().getSetting(SettingInt.class, "restapi.port").get();
		try {
			RestAPI restApi = RestAPI.newInstance(port);
			restApi.start(NanoHTTPD.SOCKET_READ_TIMEOUT, true);
			Logger.info("[REST API] Started web server on port " + port);
		} catch (Exception e) {
			Logger.error(e, "Error while starting the web server for REST API.");
			showNotification(NotificationType.ERROR, "Could not enable REST API", e.getMessage());
		}
	}
	
	public void stopRestApi() {
		RestAPI restApi = RestAPI.getInstance();
		if(restApi != null) {
			try {
				restApi.stop();
				Logger.info("[REST API] Stopped web server");
			} catch(Exception e) {
				Logger.error(e, "Error while stopping the web server for REST API.");
				getCore().showErrorNotification(e, "Could not stop REST API");
			}
		}
	}
	
	public boolean isRestApiRunning() {
		return RestAPI.getInstance() != null && RestAPI.getInstance().isAlive();
	}
	
	
	/**
	 * Register a new JFrame window.
	 * <p> Is needed to toggle the custom window decorations.
	 * @param frame	the frame to register
	 */
	public void registerFrame(JFrame frame) {
		frames.add(frame);
	}
	
	/**
	 * Remove a JFrame window from the list.
	 * @param frame	the frame to unregister
	 */
	public void unregisterFrame(JFrame frame) {
		frames.remove(frame);
	}
	
	
	public static Main getInstance() {
		return instance;
	}
	
	public RemoteLightCore getCore() {
		return remoteLightCore;
	}
	
	public MainFrame getMainFrame() {
		return mainFrame;
	}
	
	public PluginManager getPluginManager() {
		return pluginManager;
	}
	
	/**
	 * Wrapper method
	 * @return settings manager of RemoteLightCore
	 */
	public SettingsManager getSettingsManager() {
		return getCore().getSettingsManager();
	}

	/**
	 * Returns the number of LEDs of the active output
	 * @return	the set number of LEDs or the active output
	 */
	public static int getLedNum() {
		return RemoteLightCore.getLedNum();
	}
	
	/**
	 * Notification show wrapper method
	 * @param notificationType 	Notification type
	 * @param text				displayed message
	 */
	public void showNotification(NotificationType notificationType, String text) {
		remoteLightCore.showNotification(notificationType, text, Notification.NORMAL);
	}
	
	/**
	 * Notification show wrapper method
	 * @param notificationType 	Notification type
	 * @param text				displayed message
	 * @param displayTime		display time length
	 */
	public void showNotification(NotificationType notificationType, String text, int displayTime) {
		remoteLightCore.showNotification(notificationType, text, displayTime);
	}
	
	/**
	 * Notification show wrapper method
	 * @param notificationType 	Notification type
	 * @param title				notification title
	 * @param text				displayed message
	 */
	public void showNotification(NotificationType notificationType, String title, String text) {
		remoteLightCore.showNotification(new Notification(notificationType, title, text));
	}
	
	/**
	 * Notification wrapper method
	 * @param notification new notification
	 */
	public void showNotification(Notification notification) {
		remoteLightCore.showNotification(notification);
	}

}
