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

package de.lars.remotelightclient.ui.menu;

import de.lars.remotelightclient.ui.panels.MenuPanel;

/**
 * Create and instantiate a menu panel by name.
 */
public interface MenuPanelFactory {
	
	/**
	 * Get the menu panel by name.
	 * 
	 * @param name	the name defined by {@link MenuPanel#getName()}
	 * @return		a newly created instance of the menu panel, or
	 * 				{@code null} to request the next menu panel factory.
	 */
	MenuPanel getMenuPanel(String name);

}
