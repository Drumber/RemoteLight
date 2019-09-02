package de.lars.remotelightclient.settings.types;

import de.lars.remotelightclient.settings.Setting;

public class SettingInt extends Setting {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6084760644268375263L;
	private int value;

	public SettingInt(String id, String name, String description, int value) {
		super(id, name, description);
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
