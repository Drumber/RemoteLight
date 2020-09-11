package de.lars.remotelightcore;

import java.util.ArrayList;
import java.util.List;

import de.lars.remotelightcore.EffectManagerHelper.EffectType;
import de.lars.remotelightcore.event.events.types.EffectOptionsUpdateEvent;
import de.lars.remotelightcore.settings.Setting;

public abstract class Effect {
	
	private String name;
	private String displayname;
	private List<String> options;
	
	public Effect(String name) {
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
	
	public void onEnable() {}
	public void onDisable() {}
	public void onLoop() {}

}
