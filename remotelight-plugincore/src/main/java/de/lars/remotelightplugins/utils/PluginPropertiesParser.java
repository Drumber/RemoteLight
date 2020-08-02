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

	public PluginPropertiesParser(Properties prop, File jarFile) throws PluginLoadException {
		// parse main class property
		String main = parseMainClass(prop);
		if(main == null) {
			throw new PluginLoadException("The '" + DefaultProperties.MAIN.getKey()
				+ "' property in the '" + PluginManager.PLUGIN_PROPERTIES + "' file is not defined!");
		}
		
		// create a new PluginInfo instance and parse other properties
		pluginInfo = new PluginInfo(jarFile.getAbsolutePath(), main);
		parseProperties(prop);
	}
	
	private String parseMainClass(Properties prop) {
		return prop.getProperty(DefaultProperties.MAIN.getKey());
	}
	
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
