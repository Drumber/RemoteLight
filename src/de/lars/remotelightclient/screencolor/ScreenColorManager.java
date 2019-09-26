package de.lars.remotelightclient.screencolor;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import org.tinylog.Logger;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.out.OutputManager;

public class ScreenColorManager {
	
	private boolean active;
	
	public ScreenColorManager() {
		active = false;
	}
	
	public void start(int yPos, int delay, boolean invert, GraphicsDevice monitor) {
		if(!active) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					active = true;
					Logger.info("Started ScreenColor thread.");
					
					int pixels = Main.getLedNum();
					ScreenColorDetector detector = new ScreenColorDetector(pixels, monitor, yPos);
					
					while(active) {
						Color[] c = detector.getColors();
						
						if(c.length <= pixels) {
							Color[] out = c;
							
							if(invert) {
								int l = out.length;
								
								for(int i = 0; i < l; i++) {
									out[i] = c[l - 1];
									l--;
								}
							}
							OutputManager.addToOutput(out);
							
						} else {
							Logger.error("[ScreenColor] There are more color measuring points than LEDs!");
						}
						
						try {
							Thread.sleep(delay);
						} catch (InterruptedException e) {
							Logger.error(e);
						}
					}
					
					Logger.info("Stopped ScreenColor thread.");
				}
			}).start();
		}
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void stop() {
		if(active) {
			active = false;
		}
	}
	
	public static GraphicsDevice[] getMonitors() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
	}

}
