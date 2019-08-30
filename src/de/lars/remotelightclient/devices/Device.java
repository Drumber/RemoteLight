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
	public Device(String id) {
		super(id);
	}
	
	public abstract ConnectionState connect();
	public abstract ConnectionState disconnect();
	public abstract ConnectionState getConnectionState();
	
	public abstract void send(Color[] pixels);

}
