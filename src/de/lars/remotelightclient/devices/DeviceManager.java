package de.lars.remotelightclient.devices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tinylog.Logger;

import de.lars.remotelightclient.DataStorage;

public class DeviceManager {
	
	private List<Device> devices;
	
	public DeviceManager() {
		Device[] storedDevices = (Device[]) DataStorage.getData(DataStorage.DEVICES_LIST);
		if(storedDevices != null) {
			devices = new ArrayList<Device>(Arrays.asList(storedDevices));
			Logger.info("Loaded " + devices.size() + " devices.");
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
	
	public List<RaspberryPi> getRaspberryPis() {
		List<RaspberryPi> tmp = new ArrayList<RaspberryPi>();
		for(Device d : devices) {
			if(d instanceof RaspberryPi) {
				tmp.add((RaspberryPi) d);
			}
		}
		return tmp;
	}
	
	public boolean addDevice(Device device) {
		if(this.getDevice(device.getId()) != null) { //ID is already used
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
	
	public void renoveAllDevices() {
		devices.clear();
	}
	
	public void saveDevices() {
		Device[] storedDevices = new Device[devices.size()];
		storedDevices = devices.toArray(storedDevices);
		DataStorage.store(DataStorage.DEVICES_LIST, storedDevices);
		Logger.info("Saved " + storedDevices.length + " devices.");
	}

}
