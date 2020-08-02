package de.lars.remotelightplugins;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.tinylog.Logger;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightplugins.exceptions.PluginLoadException;
import de.lars.remotelightplugins.utils.JarFileFilter;
import de.lars.remotelightplugins.utils.PluginPropertiesParser;

public class PluginManager {
	public final static String PLUGIN_PROPERTIES = "plugin.properties";
	
	private final RemoteLightCore core;
	/** List of all loaded plugins */
	private final List<Plugin> loadedPlugins;
	/** plugin root directory */
	private final File pluginDir;
	
	public PluginManager(final File pluginDir, RemoteLightCore core) {
		loadedPlugins = new ArrayList<Plugin>();
		this.pluginDir = pluginDir;
		this.core = core;
	}
	
	/**
	 * Load all plugins located in the specified plugin directory
	 */
	public void loadPlugins() {
		// get all files in plugin directory
		File[] files = pluginDir.listFiles(new JarFileFilter());
		// loading all plugins from plugin dir
		for(File pluginFile : files) {
			if(pluginFile.isFile()) {
				
				try {
					loadPlugin(pluginFile);
				} catch (IOException e) {
					Logger.error(e, "Error while accessing jar file: " + pluginFile.getAbsolutePath());
				} catch (PluginLoadException e) {
					Logger.error(e, "Error while loading plugin: " + pluginFile.getAbsolutePath());
				} catch (InstantiationException e) {
					Logger.error(e, "Could not instantiate plugin main class.");
				} catch (IllegalAccessException e) {
					Logger.error(e, "Could not access plugin main class.");
				} catch (ClassNotFoundException e) {
					Logger.error(e, "Could not find plugin main class.");
				}
				
			}
		}
		
		// enabling every loaded plugin
		for(Plugin plugin : loadedPlugins) {
			if(plugin.isLoaded()) {
				plugin.onEnable();
			} else {
				Logger.warn("Plugin " + plugin.getName() + " failed to load!");
				plugin.setEnabled(false);
			}
		}
		
		Logger.info("Loaded " + loadedPlugins.size() + " plugins.");
	}
	
	/**
	 * Disable all loaded plugins
	 */
	public void disablePlugins() {
		for(Plugin plugin : loadedPlugins) {
			if(plugin.isEnabled())
				plugin.onDisable();
		}
	}
	
	/**
	 * Load a plugin from file path.
	 * 
	 * @param file absolute file path to plugin jar
	 */
	private void loadPlugin(File file) throws IOException, PluginLoadException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		// create a new class loader
		try(URLClassLoader classLoader = new URLClassLoader(new URL[] {file.toURI().toURL()});) {
			
			// try to get plugin properties file
			InputStream propIn = classLoader.getResourceAsStream(PLUGIN_PROPERTIES);
			if(propIn == null) {
				throw new PluginLoadException("Could not load '" + PLUGIN_PROPERTIES + "' file from plugin classpath.");
			}
			
			Properties prop = new Properties();
			prop.load(propIn);
			propIn.close();
			
			PluginPropertiesParser propParser = new PluginPropertiesParser(prop, file);
			PluginInfo plInfo = propParser.getPluginInfo();
			final String mainClass = plInfo.getMainClass();
			
			// instantiate and initialize plugin main class
			Plugin plugin = (Plugin) classLoader.loadClass(mainClass).newInstance();
			plugin.init(plInfo, core);
			plugin.onLoad();
			loadedPlugins.add(plugin);
			
			Logger.info("Successfully loaded plugin " + plugin.getName());
		}
	}
	
	
	public File getPluginDirectory() {
		return pluginDir;
	}
	
	public List<Plugin> getLoadedPlugins() {
		return loadedPlugins;
	}

}
