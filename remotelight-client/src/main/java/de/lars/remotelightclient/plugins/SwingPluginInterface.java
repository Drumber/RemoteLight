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

package de.lars.remotelightclient.plugins;

import javax.swing.JFrame;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightplugins.plugininterface.DefaultPluginInterface;

/**
 * Interface for communication between plugins and
 * the RemoteLight Swing application.
 */
public final class SwingPluginInterface extends DefaultPluginInterface {

	private final Main main;
	
	/**
	 * Create a new plugin interface for the RemoteLight swing application
	 * 
	 * @param main	the applications main class
	 */
	public SwingPluginInterface(final Main main) {
		super(main.getCore(), main.getPluginManager());
		this.main = main;
	}
	
	/**
	 * Get the main class of the swing application.
	 * 
	 * @return	main class instance
	 */
	public Main getMainClass() {
		return main;
	}
	
	/**
	 * Get the main frame of the swing application.
	 * 
	 * @return	the main frame (extends {@link JFrame})
	 */
	public MainFrame getMainFrame() {
		return main.getMainFrame();
	}

}
