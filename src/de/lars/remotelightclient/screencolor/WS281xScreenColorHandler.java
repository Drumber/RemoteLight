package de.lars.remotelightclient.screencolor;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.network.Client;

public class WS281xScreenColorHandler {
	
	private static boolean active;
	private static Timer timer;
	
	public static void start(int yPos, int interval, boolean invert, GraphicsDevice monitor) {
		if(!active) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					active = true;
					int pixels = Main.getLedNum();
					WS281xScreenColorDetector detector = new WS281xScreenColorDetector(pixels, monitor, yPos);
					
					timer = new Timer();
					timer.scheduleAtFixedRate(new TimerTask() {
						
						@Override
						public void run() {
							if(active) {
								Color[] c = detector.getColors();
								if(c.length <= pixels) {
									HashMap<Integer, Color> pixelHash = new HashMap<>();
									
									if(!invert) {
										for(int i = 0; i < c.length; i++) {
											pixelHash.put(i, c[i]);
										}
									} else {
										for(int i = 0; i < c.length; i++) {
											pixelHash.put((c.length - 1 - i), c[i]);
										}
									}
									
									Client.sendWS281xList(pixelHash);
								} else {
									System.out.println("[ScreenColor] ERROR: More color measuring points than LEDs!");
								}
							} else
								timer.cancel();
						}
						
					}, 0, interval);
					
				}
			}).start();
		}
	}
	
	public static void stop() {
		if(active) {
			active = false;
			timer.cancel();
		}
	}
	
	public static boolean isActive() {
		return active;
	}
	
	public static GraphicsDevice[] getMonitors() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
	}

}
