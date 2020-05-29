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
package de.lars.remotelightcore.data;

import java.io.File;
import java.io.IOException;

import org.tinylog.Logger;

import com.blogspot.debukkitsblog.util.FileStorage;

import de.lars.remotelightcore.utils.DirectoryUtil;

public class DataStorage {
	
	private static FileStorage storage;
	public static final String SETTINGSMANAGER_KEY = "settingsmanager_key";
	public final static String DEVICES_LIST = "devices_list";
	
	/**
	 * Creates data file if it does not exist
	 */
	public static void start() {
		try {
			new File(DirectoryUtil.getDataStoragePath()).mkdirs();
			File file = new File(DirectoryUtil.getDataStoragePath() + DirectoryUtil.DATA_FILE_NAME);
			storage = new FileStorage(file, true);
		} catch (IllegalArgumentException | IOException e) {
			Logger.error(e);
		}
		if(!isCreated()) Logger.error("Could not create data file!");;
	}
	
	/**
	 * 
	 * @param key Key of the setting
	 * @param data Data you want to store
	 */
	public static void store(String key, Object data) {
		try {
			if(isCreated()) {
				if(storage.hasKey(key)) {
					storage.remove(key);
				}
				storage.store(key, data);
			}
		} catch (IOException e) {
			Logger.error(e, "Could not save data!");
		}
	}
	
	/**
	 * Deletes the data of the corresponding key
	 * 
	 */
	public static void remove(String key) {
		try {
			storage.remove(key);
		} catch (IOException e) {
			Logger.warn(e, "Could not remove '" + key + "'.");
		}
	}
	
	/**
	 * Saves the data file
	 */
	public static void save() {
		if(isCreated()) {
			try {
				storage.save();
			} catch (IOException e) {
				Logger.error(e, "Could not save data file!");
			}
		}
	}
	
	/**
	 * Get data of the corresponding key
	 * @param key Key of the data
	 * @return stored data or null if key is not stored
	 */
	public static Object getData(String key) {
		try {
			if(isCreated())
				return storage.get(key);
		} catch (Exception e) {
			Logger.error(e, "Could not read data file!");
		}
		return null;
	}
	
	/**
	 * 
	 * @return FileStorage
	 */
	public static FileStorage getstorage() {
		return storage;
	}
	
	/**
	 * Checks if key is stored
	 *
	 */
	public static boolean isStored(String key) {
		try {
			Object data = DataStorage.getData(key);
			if(data != null)
				return true;
		} catch (Exception e) {
			Logger.info(e, key + " is not stored.");
		}
		return false;
	}
	
	/**
	 * Checks if data file exist
	 * 
	 */
	public static boolean isCreated() {
		if(new File(DirectoryUtil.getDataStoragePath() + DirectoryUtil.DATA_FILE_NAME).isFile()) {
			if(storage != null) {
				return true;
			}
		}
		return false;
	}

}
