package de.lars.remotelightclient.devices;

import com.fazecast.jSerialComm.SerialPort;

public class Arduino extends Device {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7893775235554866836L;
	private SerialPort comPort;

	public Arduino(String id, SerialPort comPort) {
		super(id);
		this.comPort = comPort;
	}

	public SerialPort getComPort() {
		return comPort;
	}

	public void setComPort(SerialPort comPort) {
		this.comPort = comPort;
	}

}
