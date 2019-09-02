package de.lars.remotelightclient.settings.types;

import de.lars.remotelightclient.settings.Setting;

public class SettingString extends Setting {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3626594401340860658L;
	private String value;

	public SettingString(String id, String name, String description, String value) {
		super(id, name, description);
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
