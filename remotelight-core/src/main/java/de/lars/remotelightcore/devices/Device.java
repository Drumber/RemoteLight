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

package de.lars.remotelightcore.devices;

import java.awt.Color;
import java.io.Serializable;

import de.lars.remotelightcore.devices.arduino.RgbOrder;
import de.lars.remotelightcore.out.Output;

public abstract class Device extends Output implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5542594482384646241L;
	private RgbOrder rgbOrder;
	
	/**
	 * @param id User defined name for the device
	 */
	public Device(String id, int pixels) {
		super(id, pixels);
		rgbOrder = RgbOrder.RGB;
	}
	
	public abstract ConnectionState connect();
	public abstract ConnectionState disconnect();
	public abstract ConnectionState getConnectionState();
	/**
	 * Called when Device is loaded from data file
	 */
	public abstract void onLoad();
	
	public abstract void send(Color[] pixels);
	
	@Override
	public void onOutput(Color[] pixels) {
		pixels = getOutputPatch().patchOutput(pixels, getRgbOrder());
		send(pixels);
	}
	
	public RgbOrder getRgbOrder() {
		return rgbOrder;
	}

	public void setRgbOrder(RgbOrder rgbOrder) {
		this.rgbOrder = rgbOrder;
	}

	@Override
	public ConnectionState getState() {
		return getConnectionState();
	}
	
	@Override
	public void onActivate() {
		connect();
		super.onActivate();
	}
	
	@Override
	public void onDeactivate() {
		disconnect();
		super.onDeactivate();
	}

}
