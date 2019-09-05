package de.lars.remotelightclient.devices.arduino;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import org.tinylog.Logger;

import com.fazecast.jSerialComm.SerialPort;

import de.lars.remotelightclient.devices.ConnectionState;

public class ComPort implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -269054641983915463L;

	private final int BAUD = 1000000;
	
	private SerialPort port;
	private OutputStream output;
	private boolean open;
	private ConnectionState state;
	
	public ComPort() {
		open = false;
		state = ConnectionState.DISCONNECTED;
	}
	
	public ComPort(SerialPort port) {
		open = false;
		state = ConnectionState.DISCONNECTED;
		this.setPort(port);
	}
	
	public void setPort(SerialPort port) {
		this.port = port;
	}
	
	public static SerialPort[] getComPorts() {
		SerialPort ports[] = SerialPort.getCommPorts();
		return ports;
	}
	
	public static SerialPort getComPortByName(String name) {
		for(SerialPort s : getComPorts()) {
			if(s.getSystemPortName().equals(name)) {
				return s;
			}
		}
		return null;
	}
	
	public ConnectionState openPort(SerialPort comPort) {
		if(port != null && port.isOpen()) {
			closePort();
		}
		
		this.port = comPort;
		
		if(port.openPort()) {
			open = true;
			state = ConnectionState.CONNECTED;
			Logger.info("Successfully opened ComPort: " + port.getSystemPortName());
			
			port.setBaudRate(BAUD);
			output = port.getOutputStream();
			
		} else {
			Logger.error("Could not open ComPort " + port.getSystemPortName());
			state = ConnectionState.FAILED;
			open = false;
		}
		return state;
	}
	
	public ConnectionState closePort() {
		if(open) {
			if(port != null && port.isOpen()) {
				try {
					
					Logger.info("Closed ComPort: " + port.getSystemPortName());
					
					output.close();
					port.closePort();
					open = false;
					state = ConnectionState.DISCONNECTED;
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return state;
	}
	
	public void send(byte[] outputBuffer, int size) {
		if(port.isOpen()) {
			try {
				output.write(outputBuffer, 0, size);
			} catch (IOException e) {
				Logger.error("Error while sending data kto ComPort " + port.getSystemPortName());
				e.printStackTrace();
			}
		}
	}
	
	public ConnectionState getState() {
		return state;
	}

}
