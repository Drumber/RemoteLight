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

package de.lars.remotelightcore.devices.virtual;

import java.awt.Color;

import de.lars.remotelightcore.devices.ConnectionState;
import de.lars.remotelightcore.devices.Device;

public class VirtualOutput extends Device {
	private static final long serialVersionUID = -6877178657792878944L;
	
	private transient PixelOutputStream out;

	public VirtualOutput(String id, int pixels) {
		super(id, pixels);
		out = new PixelOutputStream();
	}

	@Override
	public ConnectionState connect() {
		out.setActive(true);
		return ConnectionState.CONNECTED;
	}

	@Override
	public ConnectionState disconnect() {
		out.setActive(false);
		return ConnectionState.DISCONNECTED;
	}

	@Override
	public ConnectionState getConnectionState() {
		return out.isActive() ? ConnectionState.CONNECTED : ConnectionState.DISCONNECTED;
	}

	@Override
	public void onLoad() {
		if(out == null)
			out = new PixelOutputStream();
	}

	@Override
	public void send(Color[] pixels) {
		out.writeStrip(pixels);
	}

}
