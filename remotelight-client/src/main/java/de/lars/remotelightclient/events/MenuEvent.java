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

package de.lars.remotelightclient.events;

import de.lars.remotelightclient.ui.panels.MenuPanel;
import de.lars.remotelightcore.event.events.Event;

/**
 * MenuEvent called on menu change
 */
public class MenuEvent implements Event {
	
	private final MenuPanel menuPanel;
	private final String menuName, prevMenuName;
	
	public MenuEvent(final MenuPanel menu, String menuName, String prevMenuName) {
		this.menuPanel = menu;
		this.menuName = menuName;
		this.prevMenuName = prevMenuName;
	}

	/**
	 * Get the new menu panel
	 * 
	 * @return Returns the new menu panel
	 */
	public MenuPanel getMenuPanel() {
		return menuPanel;
	}

	/**
	 * Get the name of the new menu
	 * 
	 * @return Returns the menu name
	 */
	public String getMenuName() {
		return menuName;
	}

	/**
	 * Get the name of the previous menu
	 * 
	 * @return 	Returns the previous menu name
	 * 			or null
	 */
	public String getPrevMenuName() {
		return prevMenuName;
	}

}
