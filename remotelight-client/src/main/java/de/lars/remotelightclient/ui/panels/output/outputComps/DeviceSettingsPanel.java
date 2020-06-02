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

package de.lars.remotelightclient.ui.panels.output.outputComps;

import javax.swing.JPanel;

import de.lars.remotelightcore.devices.Device;

public abstract class DeviceSettingsPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1279445549682121981L;
	private Device device;
	private boolean setup;

	/**
	 * Create the panel.
	 */
	public DeviceSettingsPanel(Device device, boolean setup) {
		this.device = device;
		this.setup = setup;
	}
	
	public Device getDevice() {
		return device;
	}

	public boolean isSetup() {
		return setup;
	}

	/**
	 * 
	 * @return returns false if ID is empty
	 */
	public abstract boolean save();
	
	public abstract String getId();

}
