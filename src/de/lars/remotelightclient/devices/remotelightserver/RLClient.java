package de.lars.remotelightclient.devices.remotelightserver;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.tinylog.Logger;

import de.lars.remotelightclient.devices.ConnectionState;

public class RLClient {
	
	public final static int PORT = 20002;
	private String hostname;
	private boolean connected;
	private InetSocketAddress address;
	private Socket socket;
	private ObjectOutputStream out;
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
				out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
				return ConnectionState.CONNECTED;
				
			} catch (IOException e) {
				Logger.error("Error while connecting to " + hostname);
				e.printStackTrace();
				connected = false;
				this.disconnect();;
			}
		}
		return ConnectionState.FAILED;
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
		} catch (IOException e) {
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
				out.writeObject(pixels);
				out.flush();
			} catch (IOException e) {
				Logger.error("Could not send color array to server!");
				e.printStackTrace();
				this.disconnect();
			}
		}
	}

}
