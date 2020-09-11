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

package de.lars.remotelightcore.musicsync;

import java.util.ArrayList;
import java.util.List;

import de.lars.remotelightcore.EffectManagerHelper.EffectType;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.event.events.types.EffectOptionsUpdateEvent;
import de.lars.remotelightcore.musicsync.sound.SoundProcessing;
import de.lars.remotelightcore.settings.Setting;

public class MusicEffect {
	
	private String name;
	private String displayname;
	private List<String> options;
	private boolean bump;
	private float pitch;
	private double pitchTime;
	private double volume;
	private SoundProcessing soundProcessor;
	private double sensitivity;
	private double adjustment;
	private double maxSpl, minSpl, spl;
	
	/**
	 * Create a new music visualizer effect
	 * 
	 * @param name	the name of the effect (should be unique)
	 */
	public MusicEffect(String name) {
		this.name = name;
		this.displayname = name;
		this.options = new ArrayList<String>();
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
	
	/**
	 * Trigger an {@link EffectOptionsUpdateEvent}
	 */
	public void updateEffectOptions() {
		RemoteLightCore.getInstance().getEventHandler().call(new EffectOptionsUpdateEvent(EffectType.MusicSync));
	}
	
	/**
	 * Get the amount of LEDs/pixels
	 * 
	 * @return	the amount of LEDs
	 */
	public int getLeds() {
		return RemoteLightCore.getLedNum();
	}
	
	public boolean isBump() {
		return bump;
	}

	public void setBump(boolean bump) {
		this.bump = bump;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public double getPitchTime() {
		return pitchTime;
	}

	public void setPitchTime(double pitchTime) {
		this.pitchTime = pitchTime;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}
	
	public SoundProcessing getSoundProcessor() {
		return soundProcessor;
	}

	public void setSoundProcessor(SoundProcessing soundProcessor) {
		this.soundProcessor = soundProcessor;
	}

	public double getSensitivity() {
		return sensitivity;
	}

	public void setSensitivity(double sensitivity) {
		this.sensitivity = sensitivity;
	}
	
	public void setAdjustment(double adjustment) {
		this.adjustment = adjustment;
	}
	
	public double getAdjustment() {
		return adjustment;
	}

	public double getMaxSpl() {
		return maxSpl;
	}

	public void setMaxSpl(double maxSpl) {
		this.maxSpl = maxSpl;
	}

	public double getMinSpl() {
		return minSpl;
	}

	public void setMinSpl(double minSpl) {
		this.minSpl = minSpl;
	}

	public double getSpl() {
		return spl;
	}

	public void setSpl(double spl) {
		this.spl = spl;
	}

	public void onEnable() {}
	public void onDisable() {}
	public void onLoop() {}

	//TODO add onRender()

}
