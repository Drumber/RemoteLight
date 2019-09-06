package de.lars.remotelightclient.devices;

import java.awt.Color;
import java.io.Serializable;

import de.lars.remotelightclient.out.Output;

public abstract class Device extends Output implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5542594482384646241L;
	
	/**
	 * @param id User defined name for the device
	 */
	public Device(String id, int pixels) {
		super(id, pixels);
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
		send(pixels);
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
