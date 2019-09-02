package de.lars.remotelightclient.settings.types;

import java.awt.Color;

import de.lars.remotelightclient.settings.Setting;
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;

public class SettingColor extends Setting {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8427251884477182754L;
	private Color value;

	public SettingColor(String id, String name, SettingCategory category, String description, Color value) {
		super(id, name, description, category);
		this.value = value;
	}

	public Color getValue() {
		return value;
	}

	public void setValue(Color value) {
		this.value = value;
	}

}
