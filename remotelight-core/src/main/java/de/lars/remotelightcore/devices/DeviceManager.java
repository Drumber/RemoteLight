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

package de.lars.remotelightcore.devices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tinylog.Logger;

import de.lars.remotelightcore.data.DataStorage;
import de.lars.remotelightcore.devices.arduino.Arduino;
import de.lars.remotelightcore.devices.remotelightserver.RemoteLightServer;

public class DeviceManager {
	
	private List<Device> devices;
	
	public DeviceManager() {
		Device[] storedDevices = (Device[]) DataStorage.getData(DataStorage.DEVICES_LIST);
		if(storedDevices != null) {
			devices = new ArrayList<Device>(Arrays.asList(storedDevices));
			Logger.info("Loaded " + devices.size() + " devices.");
			for(Device d : devices) d.onLoad();
		} else {
			devices = new ArrayList<Device>();
		}
	}
	
	/**
	 * 
	 * @return List of all devices
	 */
	public List<Device> getDevices() {
		return devices;
	}
	
	public Device getDevice(String id) {
		for(Device d : devices) {
			if(d.getId().equals(id)) {
				return d;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @return List with all Arduinos
	 */
	public List<Arduino> getArduinos() {
		List<Arduino> tmp = new ArrayList<Arduino>();
		for(Device d : devices) {
			if(d instanceof Arduino) {
				tmp.add((Arduino) d);
			}
		}
		return tmp;
	}
	
	/**
	 * 
	 * @return List with all RemoteLightServer (Raspberry Pi's)
	 */
	public List<RemoteLightServer> getRemoteLightServer() {
		List<RemoteLightServer> tmp = new ArrayList<RemoteLightServer>();
		for(Device d : devices) {
			if(d instanceof RemoteLightServer) {
				tmp.add((RemoteLightServer) d);
			}
		}
		return tmp;
	}
	
	/**
	 * 
	 * @return true when ID is already used
	 */
	public boolean isIdUsed(String id) {
		return this.getDevice(id) != null;
	}
	
	/**
	 * 
	 * @return false when ID is already used
	 */
	public boolean addDevice(Device device) {
		if(this.isIdUsed(device.getId())) { //ID is already used
			return false;
		}
		devices.add(device);
		return true;
	}
	
	/**
	 * 
	 * Removes device
	 */
	public boolean removeDevice(Device device) {
		if(this.getDevice(device.getId()) == null) { //No device with this ID
			return false;
		}
		for(int i = 0; i < devices.size(); i++) {
			if(devices.get(i).getId().equals(device.getId())) {
				devices.remove(i);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * Deletes ALL devices
	 */
	public void removeAllDevices() {
		devices.clear();
	}
	
	/**
	 * 
	 * Stores devices in data file
	 */
	public void saveDevices() {
		Device[] storedDevices = new Device[devices.size()];
		storedDevices = devices.toArray(storedDevices);
		DataStorage.store(DataStorage.DEVICES_LIST, storedDevices);
		Logger.info("Saved " + storedDevices.length + " devices.");
	}

}
