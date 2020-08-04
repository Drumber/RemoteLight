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

package de.lars.remotelightplugins.utils;

import java.io.File;
import java.util.Properties;

import de.lars.remotelightplugins.PluginInfo;
import de.lars.remotelightplugins.PluginManager;
import de.lars.remotelightplugins.exceptions.PluginLoadException;

/**
 * Parses the plugin properties file and creates a new {@link PluginInfo} instance.
 */
public class PluginPropertiesParser {
	
	private PluginInfo pluginInfo;

	/**
	 * Create a new plugin properties file parser
	 * 
	 * @param prop		properties to parse
	 * @param jarFile	the plugin file
	 * @throws PluginLoadException
	 * 			if something is missing or could not parsed
	 */
	public PluginPropertiesParser(Properties prop, File jarFile) throws PluginLoadException {
		// parse main class property
		String main = parseMainClass(prop);
		if(main == null) {
			throw new PluginLoadException("The '" + DefaultProperties.MAIN.getKey()
				+ "' property in the '" + PluginManager.PLUGIN_PROPERTIES + "' file is not defined!");
		}
		
		// create a new PluginInfo instance and parse other properties
		pluginInfo = new PluginInfo(jarFile, main);
		parseProperties(prop);
	}
	
	/**
	 * Tries to parse the main class property
	 * 
	 * @param prop	property to parse from
	 * @return		main class string or null
	 */
	private String parseMainClass(Properties prop) {
		return prop.getProperty(DefaultProperties.MAIN.getKey());
	}
	
	/**
	 * Parse all default properties definded in {@link DefaultProperties}
	 * 
	 * @param prop	the property to parse from
	 * @throws PluginLoadException
	 * 			if something is missing or could not parsed
	 */
	private void parseProperties(Properties prop) throws PluginLoadException {
		DefaultProperties[] defaultProps = DefaultProperties.values();
		for(DefaultProperties dflprop : defaultProps) {
			if(dflprop == DefaultProperties.MAIN)
				continue; // main class property already parsed
			
			String value = prop.getProperty(dflprop.getKey());
			
			if(value == null && dflprop.isRequired()) {
				throw new PluginLoadException("Missing required property in '"
						+ PluginManager.PLUGIN_PROPERTIES + "': " + dflprop.getKey());
			}
			
			pluginInfo.setValue(dflprop, value);
		}
	}
	
	
	/**
	 * Returns a {@link PluginInfo} instance with properties
	 * from plugin property file
	 * 
	 * @return PluginInfo with set values from plugin properties
	 */
	public PluginInfo getPluginInfo() {
		return pluginInfo;
	}
	
}
