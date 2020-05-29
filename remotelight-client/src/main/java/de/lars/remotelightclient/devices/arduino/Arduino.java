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
package de.lars.remotelightclient.devices.arduino;

import java.awt.Color;

import de.lars.remotelightclient.devices.ConnectionState;
import de.lars.remotelightclient.devices.Device;

public class Arduino extends Device {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7893775235554866836L;
	private String serialPort;
	private transient ComPort out;

	public Arduino(String id, String port) {
		super(id, 0);
		this.serialPort = port;
		out = new ComPort(ComPort.getComPortByName(port));
		super.setRgbOrder(RgbOrder.GRB);
	}

	public String getSerialPort() {
		return serialPort;
	}

	public void setSerialPort(String port) {
		if(this.serialPort == null || !this.serialPort.equals(port)) {
			this.serialPort = port;
			// disconnect if previously connected to another port
			if(getConnectionState() == ConnectionState.CONNECTED) {
				disconnect();
			}
			out.setPort(ComPort.getComPortByName(port));
		}
	}

	@Override
	public void send(Color[] pixels) {
		byte[] outputBuffer = GlediatorProtocol.doOutput(pixels);
		out.send(outputBuffer, outputBuffer.length);
	}

	@Override
	public ConnectionState connect() {
		return out.openPort(ComPort.getComPortByName(serialPort));
	}

	@Override
	public ConnectionState disconnect() {
		return out.closePort();
	}

	@Override
	public ConnectionState getConnectionState() {
		return out.getState();
	}

	@Override
	public void onLoad() {
		if(out == null) {
			out = new ComPort(ComPort.getComPortByName(serialPort));
		}
	}

}
