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

package de.lars.remotelightcore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.tinylog.Logger;
import org.tinylog.configuration.Configuration;
import org.tinylog.provider.ProviderRegistry;

import de.lars.remotelightcore.animation.AnimationManager;
import de.lars.remotelightcore.cmd.CommandParser;
import de.lars.remotelightcore.cmd.ConsoleReader;
import de.lars.remotelightcore.cmd.StartParameterHandler;
import de.lars.remotelightcore.data.DataFileUpdater;
import de.lars.remotelightcore.devices.DeviceManager;
import de.lars.remotelightcore.io.FileStorage;
import de.lars.remotelightcore.lua.LuaManager;
import de.lars.remotelightcore.musicsync.MusicSyncManager;
import de.lars.remotelightcore.notification.Notification;
import de.lars.remotelightcore.notification.NotificationManager;
import de.lars.remotelightcore.notification.NotificationType;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.scene.SceneManager;
import de.lars.remotelightcore.screencolor.ScreenColorManager;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.utils.DirectoryUtil;
import de.lars.remotelightcore.utils.ExceptionHandler;

public class RemoteLightCore {
	
	private static boolean shuttingDown = false;
	private static long startMillis = System.currentTimeMillis();
	
	public final static String VERSION = "dev0.2.2";
	public final static String WEBSITE = "https://remotelight-software.blogspot.com";
	public final static String GITHUB = "https://github.com/Drumber/RemoteLight";
	
	private static RemoteLightCore instance;
	private static boolean headless;
	public static StartParameterHandler startParameter;
	private FileStorage fileStorage;
	private AnimationManager aniManager;
	private SceneManager sceneManager;
	private MusicSyncManager musicManager;
	private ScreenColorManager screenColorManager;
	private DeviceManager deviceManager;
	private OutputManager outputManager;
	private SettingsManager settingsManager;
	private EffectManagerHelper effectManagerHelper;
	private LuaManager luaManager;
	private CommandParser commandParser;
	private NotificationManager notificationManager;
	
	public RemoteLightCore(String[] args, boolean headless) {
		if(instance != null) {
			throw new IllegalStateException("RemoteLightCore is already initialized!");
		}
		
		if(args == null)
			args = new String[0];
		
		instance = RemoteLightCore.this;
		startParameter = new StartParameterHandler(args);
		RemoteLightCore.headless = headless;
		
		this.configureLogger();	// configure logger (set log path etc.)
		instance = this;
		Logger.info("Starting RemoteLightCore version " + VERSION);
		
		// set default exception handler
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
		
		// create data file
		File dataFile = new File(DirectoryUtil.getDataStoragePath() + DirectoryUtil.FILE_STORAGE_NAME);
		// instantiate file storage
		fileStorage = new FileStorage(dataFile);
		try {
			// try to load data file
			fileStorage.load();
		} catch (IOException e) {
			Logger.error(e, "Could not load data file: " + dataFile.getAbsolutePath());
		}
		
		updateDataFile(); // backwards compatibility to versions < 0.2.1
		
		settingsManager = new SettingsManager(fileStorage);
		settingsManager.load(fileStorage.KEY_SETTINGS_LIST);
		deviceManager = new DeviceManager(fileStorage, fileStorage.KEY_DEVICES_LIST);
		outputManager = new OutputManager();
		luaManager = new LuaManager();
		notificationManager = new NotificationManager();
		
		// instantiate the managers of the different modes
		aniManager = new AnimationManager();
		sceneManager = new SceneManager();
		musicManager = new MusicSyncManager();
		screenColorManager = new ScreenColorManager();
		effectManagerHelper = new EffectManagerHelper();
		
		// console cmd reader
		commandParser = new CommandParser(instance);
		commandParser.setOutputEnabled(true);
		new ConsoleReader(commandParser);
		
		new SetupHelper(instance); // includes some things that need to be executed at startup
	}
	
	public static RemoteLightCore getInstance() {
		if(instance == null)
			throw new IllegalStateException("RemoteLightCore is not initialized!");
		return instance;
	}
	
	public void registerShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				if(!shuttingDown) { //prevent calling close method twice
					instance.close(false);
				}
			}
		});
	}
	
	/**
	 * Check if RemoteLight is used headless
	 * @return true if headless, false if UI mode
	 */
	public static boolean isHeadless() {
		return headless;
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
	
	public EffectManagerHelper getEffectManagerHelper() {
		return effectManagerHelper;
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
	
	public CommandParser getCommandParser() {
		return commandParser;
	}
	
	public NotificationManager getNotificationManager() {
		return notificationManager;
	}

	/**
	 * Returns the number of LEDs of the active output
	 * @return
	 */
	public static int getLedNum() {
		if(instance.getOutputManager().getActiveOutput() != null) {
			if(instance.getOutputManager().getActiveOutput().getOutputPatch().getClone() > 0) {
				return instance.getOutputManager().getActiveOutput().getOutputPatch().getPatchedPixelNumber();
			}
			return instance.getOutputManager().getActiveOutput().getPixels();
		}
		return 0;
	}
	
	/**
	 * Returns the elapsed time in milliseconds since the program started as int value
	 * @return elapsed time in ms
	 */
	public static int getMillis() {
		long m = System.currentTimeMillis() - startMillis;
		if(m >= Integer.MAX_VALUE - 1) {	// reset on overflow
			m = 0;
			startMillis = System.currentTimeMillis();
		}
		return (int) m;
	}
	
	public void showNotification(Notification notification) {
		NotificationManager manager = getNotificationManager();
		manager.addNotification(notification);
	}
	
	public void showNotification(NotificationType type, String message, int displayTime) {
		Notification notification = new Notification(type, message);
		notification.setDisplayTime(displayTime);
		showNotification(notification);
	}
	
	public void showErrorNotification(Exception e) {
		Notification notification = new Notification(NotificationType.ERROR, "An error has occurred: " + e.getClass().getCanonicalName());
		notification.setDisplayTime(Notification.LONG);
	}
	
	
	protected void updateDataFile() {
		File file = new File(DirectoryUtil.getDataStoragePath() + DirectoryUtil.DATA_FILE_NAME);
		if(!file.exists())
			return;
		
		// backup old data file
		File backupOldFile = new File(file.getAbsolutePath() + ".old_" + VERSION);
		if(!backupOldFile.exists()) {
			try {
				Files.copy(file.toPath(), backupOldFile.toPath());
			} catch (IOException e) {
				Logger.error(e, "Could not backup old data file.");
			}
		}
		
		boolean updated = false;
		try {
			
			DataFileUpdater dataFileUpdater = new DataFileUpdater(file);
			dataFileUpdater.updateData();
			dataFileUpdater.close();
			updated = true;
			
		} catch (IOException | ClassNotFoundException e) {
			Logger.error(e, "Could not update data file. Backup and rename old data file and create new one...");
			updated = false;
		}
		
		if(!updated) {
			File backupFile = new File(file.getAbsolutePath() + ".backup_" + VERSION);
			try {
				Files.move(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				Logger.error(e, "Could not backup old data file (" + file.getAbsolutePath() + "). Please remove manually.");
			}
		}
	}
	
	
	public void close(boolean autoexit) {
		shuttingDown = true;
		try {
			// save active effect as command
			String[] activeEffect = effectManagerHelper.getActiveManagerAndEffect();
			String activeCommand = null;
			if(activeEffect[0] != null && activeEffect[1] != null) {
				activeCommand = "start " + String.join(" ", activeEffect);
				Logger.info("Saving last used effect command: " + activeCommand);
			}
			settingsManager.getSettingObject("manager.lastactive.command").setValue(activeCommand);
			
			this.getEffectManagerHelper().stopAll();// Stop all active effects
			this.getOutputManager().close();		// Close active output
			
			this.getDeviceManager().saveDevices(fileStorage, fileStorage.KEY_DEVICES_LIST);	// Save device list
			this.getSettingsManager().save(fileStorage.KEY_SETTINGS_LIST); // Save all settings
			
			try {
				// save data file
				fileStorage.save();
			} catch (IOException ioe) {
				Logger.error(ioe, "Could not save data file: " + fileStorage.getFile());
			}
			
			// copy log file and rename
			DirectoryUtil.copyAndRenameLog(
					new File(DirectoryUtil.getLogsPath() + "log.txt"),
					new SimpleDateFormat("yyyy-MM-dd_HH-mm").format(new Date().getTime()) + ".txt");
			
			try {
				ProviderRegistry.getLoggingProvider().shutdown();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			instance = null;
			if(autoexit) {
				Thread.sleep(450);
				System.exit(0);
			}
		} catch(Exception e) {
			e.printStackTrace();
			instance = null;
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
