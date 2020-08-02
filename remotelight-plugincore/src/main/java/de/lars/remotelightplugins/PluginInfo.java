package de.lars.remotelightplugins;

import java.util.HashMap;
import java.util.Map;

import de.lars.remotelightplugins.utils.DefaultProperties;

/**
 * Plugin information holder
 */
public final class PluginInfo {
	
	/** plugin properties from plugin property file */
	private final Map<DefaultProperties, String> properties;
	/** plugin jar path */
	private final String path;
	
	public PluginInfo(final String path, final String mainClass) {
		properties = new HashMap<DefaultProperties, String>();
		this.path = path;
		properties.put(DefaultProperties.MAIN, mainClass);
	}

	public String getPath() {
		return path;
	}

	public String getMainClass() {
		return properties.get(DefaultProperties.MAIN);
	}
	
	public String getValue(DefaultProperties key) {
		return properties.get(key);
	}
	
	public void setValue(DefaultProperties key, String value) {
		properties.put(key, value);
	}

}
