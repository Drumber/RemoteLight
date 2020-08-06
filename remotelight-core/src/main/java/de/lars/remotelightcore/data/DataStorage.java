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

package de.lars.remotelightcore.data;

import java.io.File;
import java.io.IOException;

import org.tinylog.Logger;

import com.blogspot.debukkitsblog.util.FileStorage;

import de.lars.remotelightcore.utils.DirectoryUtil;

/**
 * @deprecated
 * Has been replaced by the new {@link de.lars.remotelightcore.io.FileStorage}
 * system (JSON).<br>
 * Exists only to update old data files and will be removed in future versions.
 */
@Deprecated
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