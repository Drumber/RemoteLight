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

import java.awt.Color;

import org.tinylog.Logger;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.devices.ConnectionState;
import de.lars.remotelightcore.devices.Device;
import de.lars.remotelightcore.notification.Notification;
import de.lars.remotelightcore.notification.NotificationType;

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
		initComPort();
		super.setRgbOrder(RgbOrder.GRB);
	}
	
	private void initComPort() {
		if(ComPort.exists(serialPort)) {
			out = new ComPort(ComPort.getComPortByName(serialPort));
		} else {
			out = new ComPort();
			Logger.error("Could not find ComPort: " + serialPort);
			Notification noti = new Notification(NotificationType.ERROR,
					"Arduino '" + getId() + "' initialization",
					"Coult not find ComPort with name: " + serialPort);
			RemoteLightCore.getInstance().showNotification(noti);
		}
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
			initComPort();
		}
	}

}
