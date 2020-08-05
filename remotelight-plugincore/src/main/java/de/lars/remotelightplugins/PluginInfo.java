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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.lars.remotelightplugins.properties.DefaultProperties;

/**
 * Plugin information holder
 */
public final class PluginInfo {
	
	/** plugin properties from plugin property file */
	private final Map<DefaultProperties, String> properties;
	/** plugin jar path */
	private final File file;
	
	/**
	 * Create a new PluginInfo instance
	 * 
	 * @param file		the plugin jar file
	 * @param mainClass	the plugins main class
	 */
	public PluginInfo(final File file, final String mainClass) {
		properties = new HashMap<DefaultProperties, String>();
		this.file = file;
		properties.put(DefaultProperties.MAIN, mainClass);
	}

	/**
	 * Get the plugin file path
	 * 
	 * @return	plugin file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Wrapper method to get the main class from properties.
	 * 
	 * @return	the plugins main class
	 */
	public String getMainClass() {
		return properties.get(DefaultProperties.MAIN);
	}
	
	/**
	 * Wrapper method to get the plugin name from properties.
	 * 
	 * @return	the plugin name
	 */
	public String getName() {
		return properties.get(DefaultProperties.NAME);
	}
	
	/**
	 * Get the properties of the plugin.
	 * 
	 * @return	a HashMap holding all defined properties
	 * 			and their string values.
	 */
	public Map<DefaultProperties, String> getProperties() {
		return properties;
	}
	
	/**
	 * Get a value from the plugin properties.
	 * 
	 * @param key	key of the value
	 * @return		the value corresponding to the given key
	 */
	public String getValue(DefaultProperties key) {
		return properties.get(key);
	}
	
	/**
	 * Set a property value.
	 * 
	 * @param key	the property key
	 * @param value	the property value
	 */
	public void setValue(DefaultProperties key, String value) {
		properties.put(key, value);
	}
	
	/**
	 * Convert String property value to ArrayList
	 * 
	 * @param dependType	the property to convert
	 * @return ArrayList with all values split by ','. (could be empty)
	 */
	public List<String> getListProperty(DefaultProperties propType) {
		List<String> list = new ArrayList<String>();
		String value = this.getValue(propType);
		if(value != null && !value.isEmpty()) {
			String[] splitted = value.trim().split(",");
			list.addAll(Arrays.asList(splitted));
		}
		return list;
	}

}
