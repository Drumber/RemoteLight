package de.lars.remotelightclient.settings;

import java.util.ArrayList;
import java.util.List;

import de.lars.remotelightclient.DataStorage;
import de.lars.remotelightclient.settings.types.SettingSelection;
import de.lars.remotelightclient.settings.types.SettingSelection.Model;

public class SettingsManager {
	
	/**
	 * Intern: Not displayed in settings UI
	 */
	public enum SettingCategory {
		General, Others, Intern
	}
	
	private List<Setting> settings;
	
	public SettingsManager() {
		settings = new ArrayList<Setting>();
		this.registerSettings();
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
	
	private void registerSettings() {
		this.addSetting(new SettingSelection("ui.style", "Style", SettingCategory.General, "Colors of the UI",
				new String[] {"Light", "Dark"}, "Dark", Model.ComboBox));
	}

}
