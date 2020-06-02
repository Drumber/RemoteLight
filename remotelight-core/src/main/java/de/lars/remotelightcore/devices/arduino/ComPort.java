/*-
 * >===license-start
 * RemoteLight
 * ===
 * Copyright (C) 2019 - 2020 Lars O.
 * ===
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * <===license-end
 */

package de.lars.remotelightcore.devices.arduino;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import org.tinylog.Logger;

import com.fazecast.jSerialComm.SerialPort;

import de.lars.remotelightcore.devices.ConnectionState;

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
					Logger.error(e, "Could not close ComPort: " + port.getSystemPortName());
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
				Logger.error(e, "Error while sending data to ComPort " + port.getSystemPortName());
			}
		}
	}
	
	public ConnectionState getState() {
		return state;
	}

}
