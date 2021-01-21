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

package de.lars.remotelightcore.effect;

import java.util.ArrayList;
import java.util.List;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.settings.Setting;

public abstract class Effect extends AbstractEffect {
	
	private List<String> options;
	
	public Effect(String name) {
		super(name);
		this.options = new ArrayList<String>();
	}
	
	/**
	 * Register a new music effect setting.
	 * 
	 * @param <T>		setting type
	 * @param setting	the setting to register
	 * @return			the registered setting
	 */
	public <T extends Setting> T addSetting(T setting) {
		options.add(setting.getId());
		return RemoteLightCore.getInstance().getSettingsManager().addSetting(setting);
	}
	
	/**
	 * Get a setting from setting manager
	 * 
	 * @param <T>	the setting type
	 * @param type	the setting subclass
	 * @param id	the setting id
	 * @return		the setting or null if no setting with
	 * 				the given id could be found
	 */
	public <T extends Setting> T getSetting(Class<T> type, String id) {
		return RemoteLightCore.getInstance().getSettingsManager().getSetting(type, id);
	}
	
	/**
	 * Get a setting from setting manager
	 * 
	 * @param id	the id of the setting
	 * @return		the setting or null if no setting with
	 * 				the given id could be found
	 */
	public Setting getSetting(String id) {
		return RemoteLightCore.getInstance().getSettingsManager().getSettingFromId(id);
	}
	
	/**
	 * Get all settings used by this music effect
	 * 
	 * @return	a list with all setting IDs the effect uses
	 */
	public List<String> getOptions() {
		return options;
	}
	
	/**
	 * Hidden settings will not be displayed.
	 * The setting must be in the options list.
	 * 
	 * @param id		the id of the setting
	 * @param hidden	whether the setting should be hidden
	 */
	public void hideSetting(String id, boolean hidden) {
		Setting setting = getSetting(id);
		if(setting != null)
			hideSetting(setting, hidden);
	}
	
	/**
	 * Hidden settings will not be displayed.
	 * The setting must be in the options list.
	 * 
	 * @param setting	the setting to hide
	 * @param hidden	whether the setting should be hidden
	 */
	public void hideSetting(Setting setting, boolean hidden) {
		if(hidden)
			setting.addFlag(Setting.HIDDEN);
		else
			setting.removeFlag(Setting.HIDDEN);
	}
	
	@Override
	public void onEnable() {
		// update settings (e.g. for hiding options)
		onSettingUpdate();
	}
	
	public void onSettingUpdate() {
		this.updateEffectOptions();
	}
	
	public void updateEffectOptions() {}

}
