package de.lars.remotelightclient.network;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.arduino.Arduino;
import de.lars.remotelightclient.arduino.ArduinoHandler;

public class Client {
	
	private final static int PORT = 20002;
	private static boolean connected;
	private static InetSocketAddress address;
	private static Socket socket;
	private static ObjectOutputStream out;
	
	public static void connect(String hostname) {
		if(!connected) {
			try {
				connected = true;
				address = new InetSocketAddress(hostname, PORT);
				socket = new Socket();
				System.out.println("[Client] Connecting to " + address.toString());
				socket.connect(address, 5000);
				out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
//				Main.getInstance().getRgbGUI().setConnectedState();
//				Main.getInstance().getWS281xGUI().setConnectedState();
				
			} catch (IOException e) {
				e.printStackTrace();
//				Main.getInstance().getRgbGUI().setConnectionStatus("ERROR: Could not connect to Server!");
//				Main.getInstance().getWS281xGUI().setConnectionStatus("ERROR: Could not connect to Server!");
				connected = false;
				close();
			}
		}
	}
	
	public static void send(String[] message) {
//		if(Main.isArduinoMode()) {
//			ArduinoHandler.handleIdentifier(message);
//			return;
//		}
		
		if(connected) {
			try {
				out.writeObject(message);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("ERROR: Could not send message to server!");
				close();
			}
		}
	}
	public static void send(String message) {
//		if(Main.isArduinoMode()) {
//			ArduinoHandler.handleIdentifier(new String[] {message});
//			return;
//		}
		
		if(connected) {
			String[] msg = {message};
			try {
				out.writeObject(msg);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("ERROR: Could not send message to server!");
				close();
			}
		}
	}
	
	public static void sendWS281xList(HashMap<Integer, Color> pixelHash) {
//		if(Main.isArduinoMode()) {
//			Arduino.setFromPixelHash(pixelHash);
//			return;
//		}
		
		if(connected) {
			try {
				out.writeObject(pixelHash);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("ERROR: Could not send Pixel-Hashmap to server!");
				close();
			}
		}
	}
	
	public static void disconnect() {
		// turn all LEDs off
		send(new String[] {Identifier.COLOR_RED, String.valueOf(0)});
		send(new String[] {Identifier.COLOR_GREEN, String.valueOf(0)});
		send(new String[] {Identifier.COLOR_BLUE, String.valueOf(0)});
		// disconnect
		send("DISCONNECT");
		connected = false;
//		Main.getInstance().getRgbGUI().setDisconnectedState();
//		Main.getInstance().getWS281xGUI().setDisconnectedState();
		try {
			if(out != null || socket != null) {
				out.close();
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void close() {
		connected = false;
//		Main.getInstance().getRgbGUI().setDisconnectedState();
//		Main.getInstance().getWS281xGUI().setDisconnectedState();
		try {
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isConnected() {
		return connected;
	}

}
