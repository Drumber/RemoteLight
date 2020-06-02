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

package de.lars.remotelightcore.devices.link.chain;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.tinylog.Logger;

import de.lars.remotelightcore.devices.ConnectionState;
import de.lars.remotelightcore.devices.Device;

public class Chain extends Device {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5415005609912021244L;
	private List<Device> devices;

	public Chain(String id) {
		super(id, 0);
		devices = new ArrayList<>();
	}
	
	public List<Device> getDevices() {
		return devices;
	}
	
	public void addDevices(Device... devices) {
		for(Device d : devices) {
			Objects.requireNonNull(d, "Device should not be null.");
			this.devices.add(d);
		}
		updatePixelNum();
	}
	
	public void removeDevice(Device d) {
		devices.remove(d);
		updatePixelNum();
	}
	
	public void clearDevices() {
		devices.clear();
	}
	
	public void updatePixelNum() {
		int pix = 0;
		for(Device d : devices)
			pix += d.getPixels();
		super.setPixels(pix);
		Logger.debug("[Chain] Updatetd pixel number: " + super.getPixels());
	}
	

	@Override
	public ConnectionState connect() {
		for(Device d : devices) {
			d.connect();
		}
		return getConnectionState();
	}

	@Override
	public ConnectionState disconnect() {
		for(Device d : devices) {
			d.disconnect();
		}
		return getConnectionState();
	}

	@Override
	public ConnectionState getConnectionState() {
		boolean disconnected = false;
		for(Device d : devices) {
			if(d.getConnectionState() == ConnectionState.FAILED) {
				disconnect();
				return ConnectionState.FAILED;
			}
			if(d.getConnectionState() == ConnectionState.DISCONNECTED) {
				disconnected = true;
			}
		}
		return disconnected ? ConnectionState.DISCONNECTED : ConnectionState.CONNECTED;
	}

	@Override
	public void onLoad() {
		for(Device d : devices)
			d.onLoad();
	}

	@Override
	public void send(Color[] pixels) {
		if(super.getPixels() == pixels.length) {
			int index = 0;
			for(Device d : devices) {
				Color[] subArray = Arrays.copyOfRange(pixels, index, index + d.getPixels());
				d.send(subArray);
				index += d.getPixels();
			}
		} else {
			Logger.error("Wrong output packet lenght! Expected " + super.getPixels() + ", got " + pixels.length);
		}
	}

}
