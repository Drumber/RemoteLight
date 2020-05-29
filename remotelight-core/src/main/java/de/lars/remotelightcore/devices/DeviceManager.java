/*******************************************************************************
 * ______                     _       _     _       _     _   
 * | ___ \                   | |     | |   (_)     | |   | |  
 * | |_/ /___ _ __ ___   ___ | |_ ___| |    _  __ _| |__ | |_ 
 * |    // _ \ '_ ` _ \ / _ \| __/ _ \ |   | |/ _` | '_ \| __|
 * | |\ \  __/ | | | | | (_) | ||  __/ |___| | (_| | | | | |_ 
 * \_| \_\___|_| |_| |_|\___/ \__\___\_____/_|\__, |_| |_|\__|
 *                                             __/ |          
 *                                            |___/           
 * 
 * Copyright (C) 2019 Lars O.
 * 
 * This file is part of RemoteLight.
 ******************************************************************************/
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
