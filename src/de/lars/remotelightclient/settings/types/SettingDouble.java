package de.lars.remotelightclient.settings.types;

import de.lars.remotelightclient.settings.Setting;

public class SettingDouble extends Setting {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 238878694442965168L;
	private double value;

	public SettingDouble(String id, String name, String description, double value) {
		super(id, name, description);
		this.value = value;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

}
