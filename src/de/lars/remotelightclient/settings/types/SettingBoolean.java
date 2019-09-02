package de.lars.remotelightclient.settings.types;

import de.lars.remotelightclient.settings.Setting;
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;

public class SettingBoolean extends Setting {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4432151117700184493L;
	private boolean value;

	public SettingBoolean(String id, String name, SettingCategory category, String description, boolean value) {
		super(id, name, description, category);
		this.value = value;
	}
	
	public boolean getValue() {
		return value;
	}
	
	public void setValue(boolean value) {
		this.value = value;
	}

}
