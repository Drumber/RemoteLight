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
package de.lars.remotelightclient.devices.remotelightserver;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.tinylog.Logger;

import com.google.gson.Gson;

import de.lars.remotelightclient.devices.ConnectionState;

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
	private Gson gson;
	
	public RLClient(String hostname) {
		this.hostname = hostname;
		connected = false;
		gson = new Gson();
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
				out.flush();
			} catch (Exception e) {
				Logger.error(e, "Could not send color array to server!");
				this.disconnect();
			}
		}
	}
	
	public String serializeToJSON(Color[] pixels) {
		return gson.toJson(pixels);
	}

}
