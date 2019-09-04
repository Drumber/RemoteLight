package de.lars.remotelightclient.settings;

import java.io.Serializable;

import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;

public class Setting implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7324328394259004155L;
	private String name;
	private String id;
	private String description;
	private SettingCategory category;
	
	public Setting(String id, String name, String description, SettingCategory category) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.category = category;
		if(name == null) this.name = "";
		if(description == null) this.description = "";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public SettingCategory getCategory() {
		return category;
	}

	public void setCategory(SettingCategory category) {
		this.category = category;
	}
	

}
