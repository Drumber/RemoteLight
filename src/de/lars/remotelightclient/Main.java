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
import de.lars.remotelightclient.network.Client;
import de.lars.remotelightclient.network.Identifier;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.scene.SceneManager;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.utils.DirectoryUtil;

public class Main {
	
	public final static String VERSION = "0.1.2";
	public final static String WEBSITE = "https://remotelight-software.blogspot.com";
	public final static String GITHUB = "https://github.com/Drumber/RemoteLightClient";
	
	private static Main instance;
	private AnimationManager aniManager;
	private SceneManager sceneManager;
	private MusicSyncManager musicManager;
	private DeviceManager deviceManager;
	private OutputManager outputManager;
	private SettingsManager settingsManager;
	private MainFrame mainFrame;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) { e.printStackTrace(); }
		
		new Main();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				instance.close();
				try {
					ProviderRegistry.getLoggingProvider().shutdown();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public Main() {
		this.configureLogger();
		instance = this;
		DataStorage.start();
		settingsManager = new SettingsManager();
		settingsManager.load(DataStorage.SETTINGSMANAGER_KEY);
		deviceManager = new DeviceManager();
		outputManager = new OutputManager();
		
		new StartUp();
		
		aniManager = new AnimationManager();
		sceneManager = new SceneManager();
		musicManager = new MusicSyncManager();
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
	
	
	public void close() {
		//TODO
		Client.send(new String[] {Identifier.WS_ANI_STOP});
		Client.send(new String[] {Identifier.WS_COLOR_OFF});
		
		this.getDeviceManager().saveDevices();
		this.getSettingsManager().save(DataStorage.SETTINGSMANAGER_KEY);
		
		DataStorage.save();
		
		//copy log file and rename
		DirectoryUtil.copyAndRenameLog(new File(DirectoryUtil.getLogsPath() + "log.txt"), new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(new Date().getTime()) + ".txt");
		
	}
	
	private void configureLogger() {
		new File(DirectoryUtil.getDataStoragePath()).mkdir();
		new File(DirectoryUtil.getLogsPath()).mkdir();
		Configuration.set("writerF.file", DirectoryUtil.getLogsPath() + "log.txt");
	}

}
