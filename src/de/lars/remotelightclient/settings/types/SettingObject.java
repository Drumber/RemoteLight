package de.lars.remotelightclient.settings.types;

import de.lars.remotelightclient.settings.Setting;
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;

public class SettingObject extends Setting {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7792796690437700794L;
	private Object value;

	/**
	 * (!) This Setting will not be visible in settings menu
	 */
	public SettingObject(String id, String name, String description, Object value) {
		super(id, name, description, SettingCategory.Intern);
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
