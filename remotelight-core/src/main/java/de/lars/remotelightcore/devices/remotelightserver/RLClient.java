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

import java.io.BufferedOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.tinylog.Logger;

import de.lars.remotelightcore.devices.ConnectionState;
import de.lars.remotelightcore.utils.color.Color;

public class RLClient implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1077693134503577060L;
	public final static int PORT = 20002;
	private String hostname;
	private boolean connected;
	private InetSocketAddress address;
	private Socket socket;
	private PrintWriter out;
	private ConnectionState state = ConnectionState.DISCONNECTED;
	
	public RLClient(String hostname) {
		this.hostname = hostname;
		connected = false;
	}
	
	public ConnectionState connect() {
		if(!connected) {
			try {
				connected = true;
				address = new InetSocketAddress(hostname, PORT);
				socket = new Socket();
				Logger.info("[Client] Connecting to " + address.toString());
				
				socket.connect(address, 5000);
				out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));
				
				state = ConnectionState.CONNECTED;
				return state;
				
			} catch (Exception e) {
				if(e instanceof ConnectException) 
					Logger.info("Could not connect to " + hostname);
				else
					Logger.error(e, "Error while connecting to " + hostname);
				connected = false;
				this.disconnect();;
			}
		}
		state = ConnectionState.FAILED;
		return state;
	}
	
	public ConnectionState disconnect() {
		connected = false;
		state = ConnectionState.DISCONNECTED;
		Logger.info("(" + hostname + ") Disconnecting...");
		try {
			if(out != null)
				out.close();
			if(socket != null)
				socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return state;
	}
	
	public ConnectionState getState() {
		return state;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	public String getHostname() {
		return hostname;
	}
	
	public void send(Color[] pixels) {
		if(connected) {
			try {
				String json = serializeToJSON(pixels);
				out.println(json);
				if(out.checkError()) {
					// Server is disconnected
					disconnect();
				}
			} catch (Exception e) {
				Logger.error(e, "Could not send color array to server!");
				this.disconnect();
			}
		}
	}
	
	public String serializeToJSON(Color[] pixels) {
		// create JSON manually to keep support for RemoteLightServer
		// protocol should be replaced in future versions
		StringBuilder sb = new StringBuilder("[");
		for(int i = 0; i < pixels.length; i++) {
			sb.append("{\"value\":");
			sb.append(pixels[i].getRGB());
			sb.append(",\"falpha\":0.0}");
			if(i != pixels.length - 1) {
				sb.append(',');
			}
		}
		sb.append(']');
		return sb.toString();
	}

}
