/*******************************************************************************
 * ______                     _       _     _       _     _   
 * | ___ \                   | |     | |   (_)     | |   | |  
 * | |_/ /___ _ __ ___   ___ | |_ ___| |    _  __ _| |__ | |_ 
 * |    // _ \ '_ ` _ \ / _ \| __/ _ \ |   | |/ _` | '_ \| __|
 * | |\ \  __/ | | | | | (_) | ||  __/ |___| | (_| | | | | |_ 
 * \_| \_\___|_| |_| |_|\___/ \__\___\_____/_|\__, |_| |_|\__|
 *                                             __/ |          
 *                                            |___/           
 * 
 * Copyright (C) 2019 Lars O.
 * 
 * This file is part of RemoteLight.
 ******************************************************************************/
package de.lars.remotelightclient;

import java.awt.EventQueue;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.tinylog.Logger;
import org.tinylog.configuration.Configuration;
import org.tinylog.provider.ProviderRegistry;

import de.lars.remotelightclient.animation.AnimationManager;
import de.lars.remotelightclient.devices.DeviceManager;
import de.lars.remotelightclient.lua.LuaManager;
import de.lars.remotelightclient.musicsync.MusicSyncManager;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.scene.SceneManager;
import de.lars.remotelightclient.screencolor.ScreenColorManager;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.utils.DirectoryUtil;

public class Main {
	private boolean shuttingDown = false;
	
	public final static String VERSION = "pre0.2.0.7";
	public final static String WEBSITE = "https://remotelight-software.blogspot.com";
	public final static String GITHUB = "https://github.com/Drumber/RemoteLight";
	
	private static Main instance;
	private AnimationManager aniManager;
	private SceneManager sceneManager;
	private MusicSyncManager musicManager;
	private ScreenColorManager screenColorManager;
	private DeviceManager deviceManager;
	private OutputManager outputManager;
	private SettingsManager settingsManager;
	private EffectManager effectManager;
	private LuaManager luaManager;
	private MainFrame mainFrame;

	public static void main(String[] args) {
		new Main(true);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				if(!getInstance().shuttingDown) { //prevent calling close method twice
					instance.close(false);
				}
			}
		});
	}
	
	public Main(boolean uiMode) {
		this.configureLogger();			// Configure Logger (set log path etc.)
		instance = this;
		Style.loadFonts();				// Load custom fonts
		DataStorage.start();			// Load data file
		settingsManager = new SettingsManager();
		settingsManager.load(DataStorage.SETTINGSMANAGER_KEY);
		deviceManager = new DeviceManager();
		outputManager = new OutputManager();
		luaManager = new LuaManager();
		
		new StartUp();					// Includes some things that need to be executed at startup
		
		// Instantiate the managers of the different modes
		aniManager = new AnimationManager();
		sceneManager = new SceneManager();
		musicManager = new MusicSyncManager();
		screenColorManager = new ScreenColorManager();
		effectManager = new EffectManager();
		// Start UI
		if(uiMode) {
			startMainFrame();
		}
	}
	
	/**
	 * Starts the UI
	 */
	public void startMainFrame() {
		// set look and feel
		try {
			UIManager.setLookAndFeel("com.jgoodies.looks.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) { System.out.println(e.getMessage()); }
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mainFrame = new MainFrame();
					mainFrame.setVisible(true);
				} catch (Exception e) {
					Logger.error(e);
				}
			}
		});
	}
	
	public static Main getInstance() {
		return instance;
	}
	
	public AnimationManager getAnimationManager() {
		return aniManager;
	}
	
	public SceneManager getSceneManager() {
		return sceneManager;
	}
	
	public MusicSyncManager getMusicSyncManager() {
		return musicManager;
	}
	
	public ScreenColorManager getScreenColorManager() {
		return screenColorManager;
	}
	
	public EffectManager getEffectManager() {
		return effectManager;
	}
	
	public DeviceManager getDeviceManager() {
		return deviceManager;
	}
	
	public OutputManager getOutputManager() {
		return outputManager;
	}
	
	public SettingsManager getSettingsManager() {
		return settingsManager;
	}
	
	public LuaManager getLuaManager() {
		return luaManager;
	}
	
	public MainFrame getMainFrame() {
		return mainFrame;
	}
	
	/**
	 * Returns the number of LEDs of the active output
	 * @return
	 */
	public static int getLedNum() {
		if(instance.getOutputManager().getActiveOutput() != null) {
			return instance.getOutputManager().getActiveOutput().getPixels();
		}
		return 0;
	}
	
	
	public void close(boolean autoexit) {
		shuttingDown = true;
		try {
			this.getEffectManager().stopAll();		// Stop all active effects
			this.getOutputManager().close();		// Close active output
			
			this.getDeviceManager().saveDevices();	// Save device list
			this.getSettingsManager().save(DataStorage.SETTINGSMANAGER_KEY);	// Save all settings
			
			DataStorage.save();						// Save data file
			
			//copy log file and rename
			DirectoryUtil.copyAndRenameLog(new File(DirectoryUtil.getLogsPath() + "log.txt"), new SimpleDateFormat("yyyy-MM-dd_HH-mm").format(new Date().getTime()) + ".txt");
			
			try {
				ProviderRegistry.getLoggingProvider().shutdown();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(autoexit) {
				Thread.sleep(450);
				System.exit(0);
			}
		} catch(Exception e) {
			e.printStackTrace();
			if(autoexit) {
				System.exit(0);
			}
		}
	}
	
	private void configureLogger() {
		new File(DirectoryUtil.getDataStoragePath()).mkdir();
		new File(DirectoryUtil.getLogsPath()).mkdir();
		Configuration.set("writerF.file", DirectoryUtil.getLogsPath() + "log.txt");
	}

}
