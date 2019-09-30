package de.lars.remotelightclient.emulator;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.tinylog.Logger;

public class RLServerEmulator {
	
	private final static int PORT = 20002;
	private boolean running = false;
	private ServerSocket serverSocket;
	private Socket socket;
	private ObjectInputStream ois;
	private List<InputReiceiveListener> listenersInput;
	private List<ConnectionStateChangeListener> listenersState;
	
	public RLServerEmulator() {
		listenersInput = new ArrayList<>();
		listenersState = new ArrayList<>();
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
						
						ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
						
						while(running) {
							
							if(ois.readObject() instanceof Color[]) {
								onInput((Color[]) ois.readObject());
								
							} else {
								Logger.error("[Emulator] Wrong protocol format! Expected color array.");
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
				if(ois != null)
					ois.close();
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
	
	public interface InputReiceiveListener {
		public void onInputReceived(Color[] pixel);
	}
	
	public synchronized void addReceiveListener(InputReiceiveListener l) {
		listenersInput.add(l);
	}
	
	private void onInput(Color[] input) {
		for(InputReiceiveListener l : listenersInput) {
			l.onInputReceived(input);
		}
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
