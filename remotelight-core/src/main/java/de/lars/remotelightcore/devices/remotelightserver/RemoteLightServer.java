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

package de.lars.remotelightcore.devices.remotelightserver;

import java.awt.Color;

import de.lars.remotelightcore.devices.ConnectionState;
import de.lars.remotelightcore.devices.Device;

public class RemoteLightServer extends Device {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 768993097355819403L;
	private String ip;
	private transient RLClient client;

	public RemoteLightServer(String id, String ip) {
		super(id, 0);
		this.ip = ip;
		client = new RLClient(ip);
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
		client.setHostname(ip);
	}
	
	public RLClient getClient() {
		return client;
	}

	@Override
	public void send(Color[] pixels) {
		client.send(pixels);
	}

	@Override
	public ConnectionState connect() {
		return client.connect();
	}

	@Override
	public ConnectionState disconnect() {
		return client.disconnect();
	}

	@Override
	public ConnectionState getConnectionState() {
		return client.getState();
	}

	@Override
	public void onLoad() {
		if(client == null) {
			client = new RLClient(ip);
		}
	}

}
