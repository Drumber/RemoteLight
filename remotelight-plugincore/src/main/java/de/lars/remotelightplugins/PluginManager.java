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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import org.tinylog.Logger;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.event.Listener;
import de.lars.remotelightcore.event.events.Stated.State;
import de.lars.remotelightcore.event.events.types.ShutdownEvent;
import de.lars.remotelightplugins.exceptions.PluginException;
import de.lars.remotelightplugins.exceptions.PluginLoadException;
import de.lars.remotelightplugins.exceptions.PluginPropertiesException;
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
	
	/** a set of scopes the current environment includes */
	private final Set<String> applicationScopes;
	
	/** List of all class loaders used to load the plugins */
	private final List<PluginClassLoader> classLoaders;
	
	/** List of all loaded plugins */
	private final List<Plugin> loadedPlugins;
	
	/** List of plugins that could not be loaded */
	private final Map<PluginInfo, String> errorPlugins;
	
	/** plugin root directory */
	private final File pluginDir;
	
	public PluginManager(final File pluginDir, RemoteLightCore core, PluginInterface pluginInterface) {
		classLoaders = new ArrayList<PluginClassLoader>();
		loadedPlugins = new ArrayList<Plugin>();
		errorPlugins = new HashMap<PluginInfo, String>();
		applicationScopes = new HashSet<String>();
		
		this.pluginDir = pluginDir;
		this.core = core;
		this.pluginInterface = pluginInterface;
		
		// All application scopes are supported by default
		applicationScopes.add("all");
		
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
				
				boolean error = false;
				PluginInfo plInfo = null;
				try {
					// parse plugin properties file
					plInfo = loadPluginProperty(pluginFile);
					
					// check if plugin scopes match with the application scopes
					if(plInfo.getProperties().containsKey(DefaultProperties.SCOPE)) {
						List<String> scopes = plInfo.getListProperty(DefaultProperties.SCOPE);
						// only check for scopes if the list does not contain 'all' or is not empty
						if(!scopes.isEmpty() || !scopes.stream().anyMatch("all"::equalsIgnoreCase)) {
							for(String scope : scopes) {
								boolean contains = applicationScopes.stream().anyMatch(scope::equalsIgnoreCase);
								// throw exception if scope is not supported
								if(!contains) {
									String err = "The plugin does not support the application scope."
											+ "\nSupported scopes: " + String.join(", ", scopes)
											+ "\nApplication scopes: " + String.join(", ", applicationScopes);
									throw new PluginLoadException(err);
								}
							}
						}
					}
				} catch (IOException e1) {
					Logger.error(e1, "Could not load plugin file: " + pluginFile.getPath());
					error = true;
				} catch (PluginLoadException e1) {
					Logger.error(e1, "Failed to load plugin: " + pluginFile.getName());
					error = true;
				} catch (PluginPropertiesException e) {
					Logger.error(e, "Invalid or missing plugin properties: " + pluginFile.getPath());
					error = true;
				}
				
				if(plInfo == null || plInfo.getMainClass() == null) {
					Logger.error("Invalid plugin property file: " + pluginFile.getPath());
					error = true;
					// create plugin info if null
					if(plInfo == null) {
						plInfo = new PluginInfo(pluginDir, null);
					}
				}
				if(plugins.containsKey(plInfo.getName())) {
					Logger.error("Plugin with name '" + plInfo.getName() + "' already exists. Please define a unique plugin name.");
					error = true;
				}
				
				// plugin load error handling
				if(error) {
					// add to error list
					errorPlugins.put(plInfo, "Error while loading. Check console logs for details.");
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
			try {
				enablePlugin(plugin);
			} catch (PluginException e) {
				Logger.error(e, "Failed to enable plugin after loading.");
				core.showErrorNotification(e, "Plugin load Error");
				// disable plugin
				try {
					disablePlugin(plugin);
				} catch (IOException e2) {}
			}
		}
		
		if(errorPlugins.size() > 0)
			Logger.info("Could not load " + errorPlugins.size() + " plugins.");
		Logger.info("Loaded " + loadedPlugins.size() + " plugins.");
	}
	
	/**
	 * Disable all loaded plugins
	 */
	public synchronized void disablePlugins() {
		for(Plugin plugin : loadedPlugins) {
			try {
				disablePlugin(plugin);
			} catch (IOException e) {
				Logger.error(e, "Error while disabling plugin: " + plugin.getName());
			}
		}
	}
	
	/**
	 * Disable a single plugin
	 * 
	 * @param plugin		the plugin to disable
	 * @throws IOException	thrown when failed to close class loader
	 */
	public synchronized void disablePlugin(Plugin plugin) throws IOException {
		if(plugin.isEnabled()) {
			plugin.setEnabled(false);
			Logger.info("Disabled plugin '" + plugin.getName() + "'.");
			ClassLoader classLoader = plugin.getClass().getClassLoader();
			if(classLoader instanceof PluginClassLoader) {
				classLoaders.remove(classLoader);
				((PluginClassLoader) classLoader).close();
			}
		}
	}
	
	/**
	 * Enable a single (loaded) plugin.
	 * 
	 * @param plugin			the plugin to load
	 * @throws PluginException	thrown when the plugin is already enabled,
	 * 							not loaded or loaded by a wrong class loader
	 */
	public synchronized void enablePlugin(Plugin plugin) throws PluginException {
		if(plugin.isEnabled())
			throw new PluginException("Plugin '" + plugin.getName() + "' is already enabled.");
		if(!plugin.isLoaded())
			throw new PluginException("Plugin '" + plugin.getName() + "' is not loaded. (plugin#isLoaded == false)");
		if(!(plugin.getClass().getClassLoader() instanceof PluginClassLoader))
			throw new PluginException("Plugin '" + plugin.getName() + "' was loaded with wrong class loader."+
					"Expected " + PluginClassLoader.class.getName() + " but is " + plugin.getClass().getClassLoader().getClass().getName());
		plugin.setEnabled(true);
	}
	
	/**
	 * Tries to load the plugin properties file
	 * 
	 * @param file	the plugin jar
	 * @return		{@link PluginInfo} created from
	 * 				the plugins property file
	 * @throws IOException
	 * @throws PluginLoadException
	 * @throws PluginPropertiesException 
	 */
	private synchronized PluginInfo loadPluginProperty(File file) throws IOException, PluginLoadException, PluginPropertiesException {
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
		PluginClassLoader classLoader = new PluginClassLoader(file, plInfo, core, pluginInterface);
		// the plugins main class
		final String mainClass = plInfo.getMainClass();
	
		// instantiate and initialize plugin main class
		Plugin plugin = (Plugin) classLoader.loadClass(mainClass).newInstance();
		//plugin.init(plInfo, core, pluginInterface); called by the custom class loader
		classLoaders.add(classLoader);
		plugin.onLoad();
		loadedPlugins.add(plugin);
		
		Logger.info("Successfully loaded plugin " + plugin.getName());
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
	 * Get all plugins with errors
	 * 
	 * @return	Map of error plugins and their error message
	 */
	public Map<PluginInfo, String> getErrorPlugins() {
		return errorPlugins;
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
	
	/**
	 * Get the application scopes set. By default 'all' is
	 * included in the set. Remove it from the list if only
	 * one (or more) specific scopes should be accepted.
	 * 
	 * @return	a {@link Set} of scopes as String
	 */
	public Set<String> getScopes() {
		return applicationScopes;
	}

}
