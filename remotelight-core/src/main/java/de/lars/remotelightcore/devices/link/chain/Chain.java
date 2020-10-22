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

package de.lars.remotelightcore.devices.link.chain;

import de.lars.remotelightcore.utils.color.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.tinylog.Logger;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.devices.ConnectionState;
import de.lars.remotelightcore.devices.Device;
import de.lars.remotelightcore.notification.Notification;
import de.lars.remotelightcore.notification.NotificationType;

public class Chain extends Device {
	private static final long serialVersionUID = 5415005609912021244L;
	
	private transient List<Device> devices;
	private ArrayList<String> deviceIds;

	public Chain(String id) {
		super(id, 0);
		devices = new ArrayList<>();
		deviceIds = new ArrayList<String>();
	}
	
	public List<Device> getDevices() {
		return devices;
	}
	
	public void addDevices(Device... devices) {
		for(Device d : devices) {
			Objects.requireNonNull(d, "Device should not be null.");
			this.devices.add(d);
			this.deviceIds.add(d.getId());
		}
		updatePixelNum();
	}
	
	public void removeDevice(Device d) {
		devices.remove(d);
		deviceIds.remove(d.getId());
		updatePixelNum();
	}
	
	public void clearDevices() {
		devices.clear();
		deviceIds.clear();
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
		if(devices == null)
			devices = new ArrayList<Device>();
		if(deviceIds == null)
			deviceIds = new ArrayList<String>();
		
		List<String> listNotFound = new ArrayList<String>();
		// try to find saved devices by id
		for(String id : deviceIds) {
			Device d = RemoteLightCore.getInstance().getDeviceManager().getDevice(id);
			if(d != null) {
				devices.add(d);
			} else {
				listNotFound.add(id);
			}
		}
		if(listNotFound.size() > 0) {
			// remove all not found devices and show error message
			deviceIds.removeAll(listNotFound);
			String notFound = String.join(", ", listNotFound);
			Logger.warn("[Chain] Could not find devices: " + notFound);
			RemoteLightCore.getInstance().showNotification(
					new Notification(NotificationType.WARN, getId() + " (Chain)", "Could not find the following devices: " + notFound));
		}
	}

	@Override
	public void send(Color[] pixels) {
		if(pixels.length >= super.getPixels()) {
			int index = 0;
			for(Device d : devices) {
				Color[] subArray = Arrays.copyOfRange(pixels, index, index + d.getPixels());
				d.onOutput(subArray);
				index += d.getPixels();
			}
		} else {
			Logger.error("Wrong output packet lenght! Expected " + super.getPixels() + ", got " + pixels.length);
		}
	}

}
