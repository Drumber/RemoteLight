package de.lars.remotelightclient.devices.arduino;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import com.fazecast.jSerialComm.SerialPort;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.arduino.Arduino;

public class ComPortOld {
	private final static int BAUD = 1000000;
	
	private static SerialPort port = null;
	private static OutputStream output;
	private static Timer timer;
	private static boolean open = false;
	
	private static byte [] outputBuffer;
	private static int size;
	
	public static SerialPort[] getComPorts() {
		SerialPort ports[] = SerialPort.getCommPorts();
		return ports;
	}
	
	public static boolean openPort(SerialPort port) {
		if(ComPortOld.port != null && ComPortOld.port.isOpen()) {
			closePort();
		}
		
		if(port.openPort()) {
			open = true;
			ComPortOld.port = port;
			System.out.println("[COM] Successfully opened port!");
			Main.getInstance().getSettingsGUI().setComPortStatusLabel("");
			
			port.setBaudRate(BAUD);
			output = port.getOutputStream();
			
			startLoop();
			
			//startReceiving();
			return true;
		} else {
			System.out.println("[COM] Could not open port!");
			Main.getInstance().getSettingsGUI().setComPortStatusLabel("Could not open port!");
		}
		return false;
	}
	
	
	public static void send(byte[] outputBuffer, int size) {
		if(isOpen()) {
			ComPortOld.outputBuffer = outputBuffer;
			ComPortOld.size = size;
		}
	}
	
	private static void startLoop() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				timer = new Timer();
				timer.scheduleAtFixedRate(new TimerTask() {
					
					@Override
					public void run() {
						try {
							
							if(outputBuffer != null)
								output.write(outputBuffer, 0, size);
							
							
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}, 0, 50);
				
			}
		}).start();
	}
	
	private static void stopLoop() {
		timer.cancel();
	}
	
	
	public static void closePort() {
		if(isOpen()) {
			if(ComPortOld.port != null && ComPortOld.port.isOpen()) {
				try {
					
					System.out.println("[COM] Closing last opened port...");
					Arduino.setColorAll(Color.BLACK);
					
					stopLoop();
					output.close();
					ComPortOld.port.closePort();
					open = false;
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static boolean isOpen() {
		return open;
	}
	
	public static String getPortName() {
		if(isOpen())
			return port.getSystemPortName();
		return "";
	}
	
	/*
	public static void startReceiving() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				input = new Scanner(port.getInputStream());
				while(input.hasNextLine()) {
					System.out.println("[COM] >> " + input.nextLine());
				}
			}
		}).start();
	}
	*/

}
