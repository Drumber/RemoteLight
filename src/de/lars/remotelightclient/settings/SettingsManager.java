package de.lars.remotelightclient.settings;

import java.util.ArrayList;
import java.util.List;

import de.lars.remotelightclient.DataStorage;

public class SettingsManager {
	
	private List<Setting> settings;
	
	public SettingsManager() {
		settings = new ArrayList<Setting>();
	}
	
	public List<Setting> getSettings() {
		return this.settings;
	}
	
	public void addSetting(Setting setting) {
		settings.add(setting);
	}
	
	public void deleteSettings() {
		settings = new ArrayList<Setting>();
	}
	
	public void save(String key) {
		DataStorage.store(key, settings);
	}
	
	@SuppressWarnings("unchecked")
	public void load(String key) {
		settings = (List<Setting>) DataStorage.getData(key);
	}

}
