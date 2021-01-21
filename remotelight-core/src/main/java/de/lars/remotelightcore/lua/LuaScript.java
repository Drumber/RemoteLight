package de.lars.remotelightcore.lua;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.effect.AbstractEffect;
import de.lars.remotelightcore.settings.Setting;

public class LuaScript extends AbstractEffect {
	
	public final static String VAL_NAME = "SCRIPT_NAME";
	public final static String FUNC_LOOP = "onLoop";
	
	private final Globals globals;
	private final String filePath;
	private boolean isActive;
	private List<Setting> listSettings;
	
	public LuaScript(Globals globals, String filePath) {
		super(getFileName(filePath));
		this.globals = globals;
		this.filePath = filePath;
		listSettings = new ArrayList<Setting>();
	}
	
	/**
	 * Loads the lua script using the specified Globals
	 * @return		lua chunk
	 */
	public LuaValue load() {
		// reset global values
		globals.set(VAL_NAME, LuaValue.NIL);
		globals.set(FUNC_LOOP, LuaValue.NIL);
		
		isActive = true;
		return globals.loadfile(filePath);
	}
	
	/**
	 * Set whether this lua script is active or not
	 * @param isActive	active state
	 */
	protected void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	/**
	 * Check if this lua script is active
	 * @return			true if active
	 */
	public boolean isActive() {
		return isActive;
	}
	
	/**
	 * Get the file path of this lua script
	 * @return			file path as string
	 */
	public String getFilePath() {
		return filePath;
	}
	
	/**
	 * Get the name of the script. Returns the file name if the
	 * script name is not defined in lua.
	 * @return			script name or path
	 */
	@Override
	public String getName() {
		if(isActive) {
			LuaValue valName = globals.get(VAL_NAME);
			if(valName.isstring()) {
				return valName.optjstring(getFileName(filePath));
			}
		}
		return getFileName(filePath);
	}
	
	public static String getFileName(String filePath) {
		int slashIndex = filePath.lastIndexOf(File.separatorChar);
		return filePath.substring(slashIndex != -1 ? slashIndex+1 : 0);
	}
	
	/**
	 * Calls the {@code onLoop()} lua function (if present)
	 */
	@Override
	public void onLoop() {
		if(isActive) {
			LuaValue funcLoop = globals.get(FUNC_LOOP);
			if(funcLoop.isfunction()) {
				funcLoop.invoke();
			}
		}
	}
	
	/**
	 * Get all settings registered by this lua script
	 * @return			list of settings
	 */
	public List<Setting> getScriptSettings() {
		return listSettings;
	}
	
	/**
	 * Add a setting to the list AND settings manager
	 * @param setting	setting instance to add
	 */
	public void addSetting(Setting setting) {
		// add it to the settings manager
		Setting s = RemoteLightCore.getInstance().getSettingsManager().addSetting(setting);
		listSettings.add(s);
	}
	
	/**
	 * Remove a setting from the list AND settings manager.
	 * @param setting	setting instance to remove
	 */
	public void removeSetting(Setting setting) {
		boolean contained = listSettings.remove(setting);
		// remove it from settings manager only if previously added
		if(contained)
			RemoteLightCore.getInstance().getSettingsManager().removeSetting(setting.getId());
	}
	
	/**
	 * Remove the setting with the specified id from the list
	 * AND settings manager.
	 * @param id		id of the setting to remove
	 */
	public void removeSetting(String id) {
		Setting setting = getSetting(id);
		if(setting != null)
			removeSetting(setting);
	}
	
	/**
	 * Get a setting by id that is present in the list
	 * @param id		the id of the setting
	 * @return			setting with the specified id
	 * 					or null if no setting with the id
	 * 					was added to the list
	 */
	public Setting getSetting(String id) {
		for(Setting s : listSettings) {
			if(s.getId().equals(id))
				return s;
		}
		return null;
	}

}
