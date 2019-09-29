package de.lars.remotelightclient;

import java.awt.EventQueue;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.tinylog.configuration.Configuration;
import org.tinylog.provider.ProviderRegistry;

import de.lars.remotelightclient.animation.AnimationManager;
import de.lars.remotelightclient.devices.DeviceManager;
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
	
	public final static String VERSION = "pre-0.2.0.1";
	public final static String WEBSITE = "https://remotelight-software.blogspot.com";
	public final static String GITHUB = "https://github.com/Drumber/RemoteLightClient";
	
	private static Main instance;
	private AnimationManager aniManager;
	private SceneManager sceneManager;
	private MusicSyncManager musicManager;
	private ScreenColorManager screenColorManager;
	private DeviceManager deviceManager;
	private OutputManager outputManager;
	private SettingsManager settingsManager;
	private EffectManager effectManager;
	private MainFrame mainFrame;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) { e.printStackTrace(); }
		
		new Main();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				if(!getInstance().shuttingDown) { //prevent calling close method twice
					instance.close(false);
				}
			}
		});
	}
	
	public Main() {
		this.configureLogger();
		instance = this;
		Style.loadFonts();
		DataStorage.start();
		settingsManager = new SettingsManager();
		settingsManager.load(DataStorage.SETTINGSMANAGER_KEY);
		deviceManager = new DeviceManager();
		outputManager = new OutputManager();
		
		new StartUp();
		
		aniManager = new AnimationManager();
		sceneManager = new SceneManager();
		musicManager = new MusicSyncManager();
		screenColorManager = new ScreenColorManager();
		effectManager = new EffectManager();
		EventQueue.invokeLater(new Runnable() {
		public void run() {
			try {
				mainFrame = new MainFrame();
				mainFrame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
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
	
	public MainFrame getMainFrame() {
		return mainFrame;
	}
	
	public static int getLedNum() {
		if(instance.getOutputManager().getActiveOutput() != null) {
			return instance.getOutputManager().getActiveOutput().getPixels();
		}
		return 0;
	}
	
	
	public void close(boolean autoexit) {
		shuttingDown = true;
		try {
			this.getEffectManager().stopAll();
			this.getOutputManager().close();
			
			this.getDeviceManager().saveDevices();
			this.getSettingsManager().save(DataStorage.SETTINGSMANAGER_KEY);
			
			DataStorage.save();
			
			//copy log file and rename
			DirectoryUtil.copyAndRenameLog(new File(DirectoryUtil.getLogsPath() + "log.txt"), new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(new Date().getTime()) + ".txt");
			
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
