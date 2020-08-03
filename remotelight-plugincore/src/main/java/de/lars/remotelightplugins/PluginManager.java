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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.tinylog.Logger;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.event.Listener;
import de.lars.remotelightcore.event.events.Stated.State;
import de.lars.remotelightcore.event.events.types.ShutdownEvent;
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
		
		// register shutdown listener
		Listener<ShutdownEvent> shutdownEvent = event -> {
			if(event.getState() == State.PRE)
				disablePlugins();
		};
		core.getEventHandler().register(ShutdownEvent.class, shutdownEvent);
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
				plugin.setEnabled(true);
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
				plugin.setEnabled(false);
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
