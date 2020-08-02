/*-
 * >===license-start
 * RemoteLight
 * ===
 * Copyright (C) 2019 - 2020 Lars O.
 * ===
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * <===license-end
 */

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
	
	public <T extends Setting> T addSetting(T setting) {
		options.add(setting.getId());
		return RemoteLightCore.getInstance().getSettingsManager().addSetting(setting);
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
