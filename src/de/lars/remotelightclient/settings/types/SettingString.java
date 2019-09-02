package de.lars.remotelightclient.settings.types;

import de.lars.remotelightclient.settings.Setting;
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;

public class SettingString extends Setting {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3626594401340860658L;
	private String value;

	public SettingString(String id, String name, SettingCategory category, String description, String value) {
		super(id, name, description, category);
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
