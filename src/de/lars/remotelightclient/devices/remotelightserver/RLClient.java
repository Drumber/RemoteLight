package de.lars.remotelightclient.devices.remotelightserver;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.tinylog.Logger;

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
				out.writeObject(pixels);
				out.flush();
			} catch (Exception e) {
				Logger.error(e, "Could not send color array to server!");
				this.disconnect();
			}
		}
	}

}
