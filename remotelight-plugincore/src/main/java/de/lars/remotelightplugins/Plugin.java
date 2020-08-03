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

package de.lars.remotelightplugins;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightplugins.utils.DefaultProperties;

/**
 * Plugin super class.
 * <p>
 * Every plugin must extend this class.
 */
public abstract class Plugin {
	
	private PluginInfo pluginInfo;
	private RemoteLightCore core;
	private boolean enabled;

	/**
	 * This method is called on plugin load.
	 * <p>
	 * It should only be executed once.
	 */
	public void onLoad() {}
	
	/**
	 * This method is called on enabling the plugin.
	 * <p>
	 * It can be executed more than once.
	 */
	public void onEnable() {}
	
	/**
	 * This method is called on disabling the plugin.
	 * <p>
	 * It can be executed more than once.
	 */
	public void onDisable() {}
	
	/**
	 * Returns whether the plugin was loaded successfully without errors.
	 * 
	 * @return true if successfully loaded, false otherwise
	 */
	public abstract boolean isLoaded();
	
	/**
	 * Get the info holder of the plugin
	 * 
	 * @return PluginInfo instance for the plugin
	 */
	public final PluginInfo getPluginInfo() {
		return pluginInfo;
	}
	
	public final String getName() {
		if(pluginInfo != null) {
			return pluginInfo.getValue(DefaultProperties.NAME);
		}
		return null;
	}
	
	/**
	 * Returns the instance of {@link RemoteLightCore}
	 * 
	 * @return RemoteLightCore instance
	 */
	protected final RemoteLightCore getCore() {
		return core;
	}
	
	/**
	 * Enable or disable the plugin
	 * 
	 * @param enabled
	 * 		whether the plugin should be enabled or disabled
	 */
	protected final void setEnabled(boolean enabled) {
		if(this.enabled != enabled) {
			this.enabled = enabled;
			
			if(this.enabled) {
				onEnable();
			} else {
				onDisable();
			}
		}
	}
	
	/**
	 * Get the enabled state of the plugin
	 * 
	 * @return true if enabled, otherwise false
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	
	/**
	 * Initialize the plugin
	 * 
	 * @param pluginInfo
	 * 		plugin info holder
	 * @param core
	 * 		{@link RemoteLightCore} instance
	 */
	final void init(PluginInfo pluginInfo, RemoteLightCore core) {
		this.pluginInfo = pluginInfo;
		this.core = core;
	}
	
}
