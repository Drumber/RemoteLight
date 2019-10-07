/*******************************************************************************
 * ______                     _       _     _       _     _   
 * | ___ \                   | |     | |   (_)     | |   | |  
 * | |_/ /___ _ __ ___   ___ | |_ ___| |    _  __ _| |__ | |_ 
 * |    // _ \ '_ ` _ \ / _ \| __/ _ \ |   | |/ _` | '_ \| __|
 * | |\ \  __/ | | | | | (_) | ||  __/ |___| | (_| | | | | |_ 
 * \_| \_\___|_| |_| |_|\___/ \__\___\_____/_|\__, |_| |_|\__|
 *                                             __/ |          
 *                                            |___/           
 * 
 * Copyright (C) 2019 Lars O.
 * 
 * This file is part of RemoteLight.
 ******************************************************************************/
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
