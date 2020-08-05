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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import org.tinylog.Logger;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.event.Listener;
import de.lars.remotelightcore.event.events.Stated.State;
import de.lars.remotelightcore.event.events.types.ShutdownEvent;
import de.lars.remotelightplugins.exceptions.PluginLoadException;
import de.lars.remotelightplugins.plugininterface.PluginInterface;
import de.lars.remotelightplugins.properties.DefaultProperties;
import de.lars.remotelightplugins.properties.PluginPropertiesParser;
import de.lars.remotelightplugins.utils.JarFileFilter;

public class PluginManager {
	public final static String PLUGIN_PROPERTIES = "plugin.properties";
	
	/** a RemoteLightCore instance */
	private final RemoteLightCore core;
	/** PluginInterface is needed to initialize the plugins */
	private final PluginInterface pluginInterface;
	/** List of all loaded plugins */
	private final List<Plugin> loadedPlugins;
	/** List of plugins that could not be loaded */
	private final Map<PluginInfo, String> errorPlugins;
	/** plugin root directory */
	private final File pluginDir;
	
	public PluginManager(final File pluginDir, RemoteLightCore core, PluginInterface pluginInterface) {
		loadedPlugins = new ArrayList<Plugin>();
		errorPlugins = new HashMap<PluginInfo, String>();
		this.pluginDir = pluginDir;
		this.core = core;
		this.pluginInterface = pluginInterface;
		
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
		// store all plugins temporary in the HashMap
		Map<String, PluginInfo> plugins = new HashMap<String, PluginInfo>();
		
		// loading all plugins from plugin dir
		for(File pluginFile : files) {
			if(pluginFile.isFile() && pluginFile.exists()) {
				
				PluginInfo plInfo = null;
				try {
					// parse plugin properties file
					plInfo = loadPluginProperty(pluginFile);
				} catch (IOException e1) {
					Logger.error(e1, "Could not load plugin file: " + pluginFile.getPath());
				} catch (PluginLoadException e1) {
					Logger.error(e1, "Invalid or missing plugin properties: " + pluginFile.getPath());
				}
				
				if(plInfo == null || plInfo.getMainClass() == null) {
					Logger.error("Invalid plugin property file: " + pluginFile.getPath());
					continue;
				}
				if(plugins.containsKey(plInfo.getName())) {
					Logger.error("Plugin with name '" + plInfo.getName() + "' already exists. Please define a unique plugin name.");
					continue;
				}
				
				// add it to the plugins HashMap
				plugins.put(plInfo.getName(), plInfo);
			}
		}
		
		// sort plugins by dependencies
		List<PluginInfo> sortedPlugins = sortByPluginDependencies(plugins);
		
		// load plugins
		Iterator<PluginInfo> sortedIterator = sortedPlugins.iterator();
		while(sortedIterator.hasNext()) {
			PluginInfo info = sortedIterator.next();
			
			try {
				loadPlugin(info.getFile(), info);
			} catch (IOException e) {
				Logger.error(e, "Error while accessing jar file: " + info.getFile().getPath());
			} catch (InstantiationException e) {
				Logger.error(e, "Could not instantiate plugin main class: " + info.getName());
			} catch (IllegalAccessException e) {
				Logger.error(e, "Could not access plugin main class: " + info.getName());
			} catch (ClassNotFoundException e) {
				Logger.error(e, "Could not find plugin main class: " + info.getName());
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
		
		if(errorPlugins.size() > 0)
			Logger.info("Could not load " + errorPlugins.size() + " plugins.");
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
	 * Tries to load the plugin properties file
	 * 
	 * @param file	the plugin jar
	 * @return		{@link PluginInfo} created from
	 * 				the plugins property file
	 * @throws IOException
	 * @throws PluginLoadException
	 */
	private synchronized PluginInfo loadPluginProperty(File file) throws IOException, PluginLoadException {
		JarFile jarFile = new JarFile(file);
		JarEntry propEntry = jarFile.getJarEntry(PLUGIN_PROPERTIES);
		
		if(propEntry == null) {
			jarFile.close();
			throw new PluginLoadException("Could not load '" + PLUGIN_PROPERTIES + "' file from plugin classpath. (Is it missing?)");
		}
		
		// get Property from jar file
		InputStream propIn = jarFile.getInputStream(propEntry);
		Properties prop = new Properties();
		prop.load(propIn);
		
		// create new property parser
		PluginPropertiesParser propParser = new PluginPropertiesParser(prop, file);
		// create PluginInfo from parsed property
		PluginInfo plInfo = propParser.getPluginInfo();
		
		// close jar file and input stream
		if(jarFile != null)
			jarFile.close();
		if(propIn != null)
			propIn.close();
		
		return plInfo;
	}
	
	/**
	 * Load a plugin from file path.
	 * 
	 * @param file absolute file path to plugin jar
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	private synchronized void loadPlugin(File file, final PluginInfo plInfo) throws MalformedURLException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		// create a new class loader
		try(URLClassLoader classLoader = new URLClassLoader(new URL[] {file.toURI().toURL()});) {;
			// the plugins main class
			final String mainClass = plInfo.getMainClass();
		
			// instantiate and initialize plugin main class
			Plugin plugin = (Plugin) classLoader.loadClass(mainClass).newInstance();
			plugin.init(plInfo, core, pluginInterface);
			plugin.onLoad();
			loadedPlugins.add(plugin);
			
			Logger.info("Successfully loaded plugin " + plugin.getName());
		}
	}
	
	
	/**
	 * Sort the plugins so that the required dependencies are loaded first
	 * 
	 * @param plugins 	all plugins
	 * @return			sorted list available plugins
	 */
	private List<PluginInfo> sortByPluginDependencies(Map<String, PluginInfo> plugins) {
		// Sorting in 3 steps:
		//	- Search for all dependencies and add them to the dependPlugins list.
		//	- Sort the dependPlugins list: Dependencies that are required by other dependencies at the beginning of the list
		//	- Add the dependPlugins to the beginning of the sorted plugins list
		
		LinkedList<PluginInfo> unsortedInfo = new LinkedList<PluginInfo>(plugins.values());
		LinkedList<PluginInfo> dependPlugins = new LinkedList<PluginInfo>();
		// missing dependencies: <Dependency Name | List of plugins needing this dependency>
		Map<String, List<PluginInfo>> missingDependencies = new HashMap<String, List<PluginInfo>>();
		// black list of plugin that can not be loaded
		List<PluginInfo> blackList = new ArrayList<PluginInfo>();

		Iterator<PluginInfo> iterator = unsortedInfo.iterator();
		while (iterator.hasNext()) {

			PluginInfo info = iterator.next();
			List<String> dependencies = info.getListProperty(DefaultProperties.DEPENDENCIES);
			List<String> softDependencies = info.getListProperty(DefaultProperties.SOFTDEPENDENCIES);

			// loop through all soft dependencies
			for (String softDep : softDependencies) {
				PluginInfo sdInfo = plugins.get(softDep); // softdependency Info
				
				if (sdInfo == null || blackList.contains(sdInfo)) {
					Logger.info("Missing soft-dependency '" + softDep + "' for plugin " + info.getName());
					// its a soft dependency (not required), so continue
					continue;
				}

				if (sdInfo != null) {
					if (!dependPlugins.contains(sdInfo) && !blackList.contains(sdInfo)) {
						// add to dependency plugins list
						dependPlugins.add(sdInfo);
					}
				}
			}

			// loop through all hard dependencies
			for (String dep : dependencies) {
				PluginInfo dInfo = plugins.get(dep); // dependency Info
				
				if (dInfo == null || blackList.contains(dInfo)) {
					// add missing dependency
					if (!missingDependencies.containsKey(dep)) {
						missingDependencies.put(dep, new ArrayList<PluginInfo>());
					}
					missingDependencies.get(dep).add(info);
					// remove dependency from list if its dependency is not available
					if(dependPlugins.contains(info)) {
						dependPlugins.remove(info);
					}
					// add to blacklist, so it can not be re-added by other plugins
					if(!blackList.contains(info)) {
						blackList.add(info);
					}
					continue;
				}

				if (dInfo != null) {
					// check if plugin is not already in dependency list and it is not blacklisted
					if (!dependPlugins.contains(dInfo) && !blackList.contains(dInfo)) {
						// add to dependency plugins list
						dependPlugins.add(dInfo);
					}
				}
			}
		}
		
		// sort dependency list
		if(dependPlugins.size() > 1) {
			boolean sorted = false;
			int curIndex = 0;
			
			while(!sorted) {
				if(curIndex >= dependPlugins.size()) {
					// finish sorting
					sorted = true;
					break;
				}
				
				// current dependency plugin info
				PluginInfo curInfo = dependPlugins.get(curIndex);
				// add all (soft and hard) dependencies to ONE list
				List<String> allDependencies = new ArrayList<String>();
				allDependencies.addAll(curInfo.getListProperty(DefaultProperties.DEPENDENCIES));
				allDependencies.addAll(curInfo.getListProperty(DefaultProperties.SOFTDEPENDENCIES));
				// remove possible duplicates
				List<String> dependencies = allDependencies.stream().distinct().collect(Collectors.toList());
				
				// dependency of the current dependency
				for(String dep : dependencies) {
					// get PluginInfo of dependency
					PluginInfo depInfo = plugins.get(dep);
					if(depInfo != null && dependPlugins.contains(depInfo)) {
						
						// check if dependency if before current
						if(dependPlugins.indexOf(depInfo) < curIndex) {
							// before current -> continue
							continue;
						}
						
						// put dependency to the front
						if(dependPlugins.remove(depInfo)) {
							dependPlugins.addFirst(depInfo);
							// set index to -1 (will be later increment to 0)
							// because we moved one to the start, we need to start
							// again at the first index
							curIndex = -1;
						}
					}
				}
				// increment index
				curIndex++;
			}
		}
		
		// process missing dependencies
		if(missingDependencies.size() > 0) {
			String errorMessage = "There are " + missingDependencies.size() + " missing dependencies: ";
			// loop through all missing dependency entries
			for(Map.Entry<String, List<PluginInfo>> entry : missingDependencies.entrySet()) {
				List<PluginInfo> infoList = entry.getValue();
				
				for(PluginInfo info : infoList) {
					// remove from list
					unsortedInfo.remove(info);
					// add to error list
					if(!errorPlugins.containsKey(info)) {
						errorPlugins.put(info, "missing dependency " + entry.getKey());
					} else {
						String msg = errorPlugins.get(info) + ", " + entry.getKey();
						errorPlugins.replace(info, msg);
					}
				}
				
				String nameList = infoList.stream().map(PluginInfo::getName).collect(Collectors.joining(", "));
				if(plugins.containsKey(entry.getKey())) {
					// installed but missing dependency
					errorMessage += String.format("%n > %s (installed) required by %s", entry.getKey(), nameList);
				} else {
					// not installed
					errorMessage += String.format("%n > %s required by %s", entry.getKey(), nameList);
				}
			}
			// print error message
			Logger.error(errorMessage);
		}
		
		// create sorted List
		List<PluginInfo> sorted = new LinkedList<PluginInfo>();
		// add dependencies first
		sorted.addAll(0, dependPlugins);
		// add remaining
		for(PluginInfo info : unsortedInfo) {
			if(!sorted.contains(info)) {
				sorted.add(info);
			}
		}
		
		return sorted;
	}
	
	
	/**
	 * Get the plugin root directory.
	 * 
	 * @return 	the plugin directory as {@link File}
	 */
	public File getPluginDirectory() {
		return pluginDir;
	}
	
	/**
	 * Get all loaded plugins
	 * 
	 * @return 	ArrayList of all loaded plugins
	 */
	public List<Plugin> getLoadedPlugins() {
		return loadedPlugins;
	}
	
	/**
	 * Get the plugin by name.
	 * 
	 * @param name	the plugin name (case sensitive)
	 * @return		the plugin with the specified name,
	 * 				or {@code null} if the plugin could
	 * 				not be found
	 */
	public Plugin getPlugin(String name) {
		for(Plugin plugin : getLoadedPlugins()) {
			if(plugin.getName().equals(name))
				return plugin;
		}
		return null;
	}
	
	/**
	 * Get the plugin interface used to initialize new plugins.
	 * 
	 * @return	the plugin interface
	 */
	public PluginInterface getPluginInterface() {
		return pluginInterface;
	}

}
