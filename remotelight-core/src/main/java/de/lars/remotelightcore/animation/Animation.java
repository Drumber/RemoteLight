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
package de.lars.remotelightcore.animation;

import java.util.ArrayList;
import java.util.List;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.settings.Setting;

public class Animation {
	
	private String name;
	private String displayname;
	private int delay;
	private boolean adjustable;
	private List<String> options;
	
	/**
	 * Animation with adjustable speed
	 */
	public Animation(String name) {
		this.name = name;
		this.displayname = name; //TODO Language system
		adjustable = true;
		options = new ArrayList<String>();
	}
	
	/**
	 * Animation with pre-defined speed (not adjustable)
	 */
	public Animation(String name, int delay) {
		this.name = name;
		this.displayname = name; //TODO Language system
		this.delay = delay;
		adjustable = false;
		options = new ArrayList<String>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayname() {
		return displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}
	
	public boolean isAdjustable() {
		return this.adjustable;
	}
	
	public int getDelay() {
		return this.delay;
	}
	
	public void addSetting(Setting setting) {
		RemoteLightCore.getInstance().getSettingsManager().addSetting(setting);
		options.add(setting.getId());
	}
	
	public Setting getSetting(String id) {
		return RemoteLightCore.getInstance().getSettingsManager().getSettingFromId(id);
	}
	
	/**
	 * @return A list with all setting IDs the animation use
	 */
	public List<String> getOptions() {
		return options;
	}
	
	public void onEnable() {}
	public void onDisable() {}
	public void onLoop() {}

}
