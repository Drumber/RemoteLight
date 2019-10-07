package de.lars.remotelightclient;

import java.io.File;
import java.io.IOException;

import org.tinylog.Logger;

import com.blogspot.debukkitsblog.util.FileStorage;

import de.lars.remotelightclient.utils.DirectoryUtil;

public class DataStorage {
	
	private static FileStorage storage;
	public static final String SETTINGSMANAGER_KEY = "settingsmanager_key";
	public final static String IP_STOREKEY = "serverip";
	public final static String SOUND_INPUT_STOREKEY = "soundinput";
	public final static String DEVICES_LIST = "devices_list";
	
	public static void start() {
		try {
			new File(DirectoryUtil.getDataStoragePath()).mkdirs();
			storage = new FileStorage(new File(DirectoryUtil.getDataStoragePath() + DirectoryUtil.DATA_FILE_NAME), true);
		} catch (IllegalArgumentException | IOException e) {
			Logger.error(e);
		}
		if(!isCreated()) Logger.error("Could not create data file!");;
	}
	
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
	
	public static void remove(String key) {
		try {
			storage.remove(key);
		} catch (IOException e) {
			Logger.warn(e, "Could not remove '" + key + "'.");
		}
	}
	
	public static void save() {
		if(isCreated()) {
			try {
				storage.save();
			} catch (IOException e) {
				Logger.error(e, "Could not save data file!");
			}
		}
	}
	
	public static Object getData(String key) {
		try {
			if(isCreated())
				return storage.get(key);
		} catch (Exception e) {
			Logger.error(e, "Could not read data file!");
		}
		return null;
	}
	
	public static FileStorage getstorage() {
		return storage;
	}
	
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
	
	public static boolean isCreated() {
		if(new File(DirectoryUtil.getDataStoragePath() + DirectoryUtil.DATA_FILE_NAME).isFile())
			return true;
		else
			return false;
	}

}
