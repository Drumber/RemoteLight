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

package de.lars.remotelightclient.ui.panels.tools;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;

public abstract class ToolsPanelEntry {
	
	/**
	 * Return the name of this entry.
	 * @return		the name as String
	 */
	public abstract String getName();
	
	/**
	 * Optional entry icon.
	 * @return		the icon or null
	 */
	public Icon getIcon() {
		return null;
	};
	
	/**
	 * Optional action buttons.
	 * @return		array of JButtons or null
	 */
	public JButton[] getActionButtons() {
		return null;
	}
	
	/**
	 * Optional panel that is shown (if not null) when the entry is clicked.
	 * @return		the menu JPanel or null
	 */
	public JPanel getMenuPanel() {
		return null;
	}
	
	/**
	 * Called when the entry is clicked.
	 */
	public void onClick() {}
	
	/**
	 * Called when the menu panel gets closed.
	 */
	public void onEnd() {}
	
}
