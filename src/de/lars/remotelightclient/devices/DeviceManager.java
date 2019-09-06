package de.lars.remotelightclient.devices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tinylog.Logger;

import de.lars.remotelightclient.DataStorage;
import de.lars.remotelightclient.devices.arduino.Arduino;
import de.lars.remotelightclient.devices.remotelightserver.RemoteLightServer;

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
	
	public List<Arduino> getArduinos() {
		List<Arduino> tmp = new ArrayList<Arduino>();
		for(Device d : devices) {
			if(d instanceof Arduino) {
				tmp.add((Arduino) d);
			}
		}
		return tmp;
	}
	
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
	 * @return returns true when ID is already used
	 */
	public boolean isIdUsed(String id) {
		return this.getDevice(id) != null;
	}
	
	/**
	 * 
	 * @return returns false when ID is already used
	 */
	public boolean addDevice(Device device) {
		if(this.isIdUsed(device.getId())) { //ID is already used
			return false;
		}
		devices.add(device);
		return true;
	}
	
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
	
	public void removeAllDevices() {
		devices.clear();
	}
	
	public void saveDevices() {
		Device[] storedDevices = new Device[devices.size()];
		storedDevices = devices.toArray(storedDevices);
		DataStorage.store(DataStorage.DEVICES_LIST, storedDevices);
		Logger.info("Saved " + storedDevices.length + " devices.");
	}

}
