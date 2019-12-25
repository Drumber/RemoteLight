package de.lars.remotelightclient.devices.link.chain;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.tinylog.Logger;

import de.lars.remotelightclient.devices.ConnectionState;
import de.lars.remotelightclient.devices.Device;

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
		devices.add(d);
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
		Logger.debug("Updatetd pixelnum: " + super.getPixels());
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
			Logger.debug(">> New output array! Lenghth: " + pixels.length);
			for(Device d : devices) {
				Logger.debug("Device " + d.getId() + " Index: " + index + " Pixels: " + d.getPixels());
				Color[] subArray = Arrays.copyOfRange(pixels, index, index + d.getPixels());
				Logger.debug("Subarray lenghth: " + subArray.length);
				d.send(subArray);
				index += d.getPixels();
			}
			Logger.debug("<< END");
		} else {
			Logger.error("Wrong output packet lenght! Expected " + super.getPixels() + ", got " + pixels.length);
		}
	}

}
