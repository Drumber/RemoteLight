package de.lars.remotelightclient.devices.arduino;

import java.awt.Color;

import com.fazecast.jSerialComm.SerialPort;

import de.lars.remotelightclient.devices.ConnectionState;
import de.lars.remotelightclient.devices.Device;

public class Arduino extends Device {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7893775235554866836L;
	private SerialPort serialPort;
	private ComPort out;

	public Arduino(String id, SerialPort port) {
		super(id, 0);
		this.serialPort = port;
		out = new ComPort(port);
	}

	public SerialPort getSerialPort() {
		return serialPort;
	}

	public void setSerialPort(SerialPort port) {
		this.serialPort = port;
		out.setPort(port);
	}

	@Override
	public void send(Color[] pixels) {
		byte[] outputBuffer = GlediatorProtocol.doOutput(pixels);
		out.send(outputBuffer, outputBuffer.length);
	}

	@Override
	public ConnectionState connect() {
		return out.openPort(serialPort);
	}

	@Override
	public ConnectionState disconnect() {
		return out.closePort();
	}

	@Override
	public ConnectionState getConnectionState() {
		return out.getState();
	}

}
