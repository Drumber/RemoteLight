package de.lars.remotelightclient.settings.types;

import java.awt.Color;

import de.lars.remotelightclient.settings.Setting;

public class SettingColor extends Setting {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8427251884477182754L;
	private Color value;

	public SettingColor(String id, String name, String description, Color value) {
		super(id, name, description);
		this.value = value;
	}

	public Color getValue() {
		return value;
	}

	public void setValue(Color value) {
		this.value = value;
	}

}
