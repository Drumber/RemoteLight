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
package de.lars.remotelightclient.settings;

import java.util.ArrayList;
import java.util.List;

import org.tinylog.Logger;

import de.lars.remotelightclient.DataStorage;
import de.lars.remotelightclient.settings.types.SettingObject;

public class SettingsManager {
	
	/**
	 * Intern + MusicEffect: Not displayed in settings UI
	 */
	public enum SettingCategory {
		General, Others, Intern, MusicEffect
	}
	
	private List<Setting> settings;
	
	public SettingsManager() {
		settings = new ArrayList<Setting>();
	}
	
	/**
	 * 
	 * @return A list with all settings
	 */
	public List<Setting> getSettings() {
		return this.settings;
	}
	
	/**
	 * 
	 * @return A list with all settings from defined category
	 */
	public List<Setting> getSettingsFromCategory(SettingCategory category) {
		List<Setting> tmp = new ArrayList<Setting>();
		for(Setting s : settings) {
			if(s.getCategory() == category) {
				tmp.add(s);
			}
		}
		return tmp;
	}
	
	public Setting getSettingFromId(String id) {
		for(Setting s : settings) {
			if(s.getId().equals(id)) {
				return s;
			}
		}
		return null;
	}
	
	public SettingObject getSettingObject(String id) {
		return getSettingFromType(new SettingObject(id, null, null));
	}
	
	/**
	 * 
	 * @param type Setting subclass WITH defined ID
	 */
	@SuppressWarnings("unchecked")
	public <T extends Setting> T getSettingFromType(T type) {
		if(type.getId() != null) {
			return (T) getSettingFromId(type.getId());
		}
		return null;
	}
	
	/**
	 * Checks if setting with defined ID is registered
	 */
	public boolean isRegistered(String id) {
		return getSettingFromId(id) != null;
	}
	
	/**
	 * Register setting if not already registered
	 */
	public void addSetting(Setting setting) {
		if(getSettingFromId(setting.getId()) != null) {
			return;
		}
		Logger.info("Registered Setting '" + setting.getId() + "'.");
		settings.add(setting);
	}
	
	/**
	 * Remove setting if available
	 */
	public void removeSetting(String id) {
		if(getSettingFromId(id) != null) {
			for(int i = 0; i < settings.size(); i++) {
				if(settings.get(i).getId().equals(id)) {
					settings.remove(i);
					Logger.info("Removed Setting '" + id + "'.");
					break;
				}
			}
		}
	}
	
	/**
	 * Deletes ALL settings
	 */
	public void deleteSettings() {
		settings = new ArrayList<Setting>();
	}
	
	/**
	 * Stores the settings in the data file
	 * @param key DataStorage Key
	 */
	public void save(String key) {
		DataStorage.store(key, settings);
		Logger.info("Stored " + settings.size() + " setting to data file.");
	}
	
	/**
	 * Loads settings from data file
	 * @param key DataStorage Key
	 */
	@SuppressWarnings("unchecked")
	public void load(String key) {
		if(DataStorage.getData(key) != null && DataStorage.getData(key) instanceof List<?>) {
			settings = (List<Setting>) DataStorage.getData(key);
			Logger.info("Loaded " + settings.size() + " settings from data file.");
		}
	}

}
