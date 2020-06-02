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

package de.lars.remotelightclient.simulator;

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

import de.lars.remotelightclient.lang.i18n;

public class RLServerSimulator {
	
	private final static int PORT = 20002;
	private boolean running = false;
	private ServerSocket serverSocket;
	private Socket socket;
	private Scanner scanner;
	private Gson gson;
	private Color[] inputPixels;
	private List<ConnectionStateChangeListener> listenersState;
	
	public RLServerSimulator() {
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
						Logger.info("[Simulator] Wating for connection...");
						onStateChanged(i18n.getString("RLServerEmulator.WaitingConnection")); //$NON-NLS-1$
						
						socket = serverSocket.accept();
						Logger.info("[Simulator] Client connected: " + socket.getRemoteSocketAddress());
						onStateChanged(i18n.getString("RLServerEmulator.Connected")); //$NON-NLS-1$
						
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
			}, "Simulator thread").start();
		}
	}
	
	public void stop() {
		if(running) {
			running = false;
			onStateChanged(i18n.getString("RLServerEmulator.Disconnected")); //$NON-NLS-1$
			try {
				if(scanner != null)
					scanner.close();
				if(socket != null)
					socket.close();
				if(serverSocket != null)
					serverSocket.close();
				Logger.info("[Simulator] Server stopped...");
			} catch (IOException e) {
				Logger.error(e, "[Simulator] Could not stop the Server!");
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
