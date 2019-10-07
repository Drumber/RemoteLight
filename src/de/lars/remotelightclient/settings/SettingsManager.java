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
	 * Intern, MusicEffect: Not displayed in settings UI
	 */
	public enum SettingCategory {
		General, Others, Intern, MusicEffect
	}
	
	private List<Setting> settings;
	
	public SettingsManager() {
		settings = new ArrayList<Setting>();
	}
	
	public List<Setting> getSettings() {
		return this.settings;
	}
	
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
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends Setting> T getSettingFromType(T type) {
		if(type.getId() != null) {
			return (T) getSettingFromId(type.getId());
		}
		return null;
	}
	
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
	
	public void deleteSettings() {
		settings = new ArrayList<Setting>();
	}
	
	public void save(String key) {
		DataStorage.store(key, settings);
		Logger.info("Stored " + settings.size() + " setting to data file.");
	}
	
	@SuppressWarnings("unchecked")
	public void load(String key) {
		if(DataStorage.getData(key) != null && DataStorage.getData(key) instanceof List<?>) {
			settings = (List<Setting>) DataStorage.getData(key);
			Logger.info("Loaded " + settings.size() + " settings from data file.");
		}
	}

}
