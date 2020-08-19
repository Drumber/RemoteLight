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

package de.lars.remotelightclient.ui.panels.tools.entrypanels;

import java.awt.EventQueue;

import javax.swing.JFrame;

import de.lars.remotelightclient.ui.console.ConsoleFrame;
import de.lars.remotelightclient.ui.panels.tools.ToolsPanelEntry;

public class ConsoleEntryPanel extends ToolsPanelEntry {
	
	private ConsoleFrame console;

	@Override
	public String getName() {
		return "Console";
	}
	
	@Override
	public void onClick() {
		EventQueue.invokeLater(() -> {
			if(console == null) {
				console = new ConsoleFrame();
			} else if(!console.isVisible()) {
				console.setVisible(true);
			}
			if(console.getState() == JFrame.ICONIFIED) {
				console.setState(JFrame.NORMAL);
			}
			if (console.isVisible() && console.isDisplayable()) {
				console.requestFocus();
			}
		});
	}

}
