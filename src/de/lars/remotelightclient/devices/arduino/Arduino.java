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
	}

	public String getSerialPort() {
		return serialPort;
	}

	public void setSerialPort(String port) {
		this.serialPort = port;
		out.setPort(ComPort.getComPortByName(port));
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
