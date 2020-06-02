/*-
 * >===license-start
 * RemoteLight
 * ===
 * Copyright (C) 2019 - 2020 Drumber
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

package de.lars.remotelightclient.ui.listeners;

import de.lars.remotelightclient.ui.panels.controlbars.ControlBar;

public interface ControlBarListener {
	
	/**
	 * Triggered when the visibility of the control bar is changed.
	 * @param visible whether the control bar is visible or hidden
	 */
	void onControlBarVisibilityChange(boolean visible);
	
	/**
	 * Triggered when the control bar is changed, i.e. replaced by another one or null.
	 * @param controlBar new control bar or null
	 */
	void onControlBarChange(ControlBar controlBar);

}
