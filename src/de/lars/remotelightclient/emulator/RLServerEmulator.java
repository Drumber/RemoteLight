package de.lars.remotelightclient.emulator;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.tinylog.Logger;

import com.google.gson.Gson;

public class RLServerEmulator {
	
	private final static int PORT = 20002;
	private boolean running = false;
	private ServerSocket serverSocket;
	private Socket socket;
	private Scanner scanner;
	private Gson gson;
	private Color[] inputPixels;
	private List<ConnectionStateChangeListener> listenersState;
	
	public RLServerEmulator() {
		listenersState = new ArrayList<>();
		gson = new Gson();
	}
	
	public void start() {
		if(!running) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					try {
						serverSocket = new ServerSocket(PORT);
						running = true;
						Logger.info("[Emulator] Wating for connection...");
						onStateChanged("Waiting for connection...");
						
						socket = serverSocket.accept();
						Logger.info("[Emulator] Client connected: " + socket.getRemoteSocketAddress());
						onStateChanged("Connected");
						
						scanner = new Scanner(new BufferedInputStream(socket.getInputStream()));
						
						while(running) {
							
							if(scanner.hasNextLine()) {
								String input = scanner.nextLine();
								inputPixels = gson.fromJson(input, Color[].class);
							}
						}
						
					} catch (Exception e) {
						Logger.error(e);
					}
				}
			}, "Emulator thread").start();
		}
	}
	
	public void stop() {
		if(running) {
			running = false;
			onStateChanged("Disconnected");
			try {
				if(scanner != null)
					scanner.close();
				if(socket != null)
					socket.close();
				if(serverSocket != null)
					serverSocket.close();
				Logger.info("[Emulator] Server stopped...");
			} catch (IOException e) {
				Logger.error(e, "[Emulator] Could not stop the Server!");
			}
		}
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public Color[] getPixels() {
		return inputPixels;
	}
	
	public interface ConnectionStateChangeListener {
		public void onConnectionStateChanged(String status);
	}
	
	public synchronized void addStateChangeListener(ConnectionStateChangeListener l) {
		listenersState.add(l);
	}
	
	private void onStateChanged(String text) {
		for(ConnectionStateChangeListener l : listenersState) {
			l.onConnectionStateChanged(text);
		}
	}

}
