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
import java.net.URL;
import java.net.URLClassLoader;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightplugins.exceptions.PluginInitException;
import de.lars.remotelightplugins.plugininterface.PluginInterface;

/**
 * Custom plugin class loader. <p>Extends {@link URLClassLoader}
 */
public class PluginClassLoader extends URLClassLoader {
	
	private final File file;
	private final PluginInfo plInfo;
	private final RemoteLightCore core;
	private final PluginInterface plInterface;
	private Plugin plugin;

	public PluginClassLoader(File file, PluginInfo plInfo, RemoteLightCore core, PluginInterface plInterface) throws IOException {
		super(new URL[] {file.toURI().toURL()});
		this.file = file;
		this.plInfo = plInfo;
		this.core = core;
		this.plInterface = plInterface;
	}
	
	/**
	 * Initialize the plugin
	 * @param plugin	plugin to initialize
	 * @throws PluginInitException
	 * 		thrown when the plugin initialization fails
	 */
	protected synchronized void initPlugin(Plugin plugin) throws PluginInitException {
		if(plugin == null)
			throw new PluginInitException("Plugin is 'null'.");
		if(plugin.getClass().getClassLoader() != this)
			throw new PluginInitException("Wrong class loader! Expected "
					+ PluginClassLoader.class.getName() + " but is " + plugin.getClass().getClassLoader().getClass().getName());
		if(this.plugin != null)
			throw new PluginInitException("Plugin '" + this.plugin.getName() +  "' is already initialized!");
		plugin.init(plInfo, core, plInterface);
		this.plugin = plugin;
		//Logger.debug("Initialized plugin " + plugin.getName());
	}
	
	public File getFile() {
		return file;
	}
	
	public Plugin getPlugin() {
		return plugin;
	}

}
