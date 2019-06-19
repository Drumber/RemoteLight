package de.lars.remotelightclient.scenes;

import java.awt.Color;
import java.util.HashMap;
import java.util.Random;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.network.Client;

public class Fire {
	
	/*
	 * adapted from WALLTECH (YT: https://www.youtube.com/channel/UCttR-Mx2i9-0og7Dyeqlc7g)
	 * https://web.archive.org/web/20180721214611/www.walltech.cc/neopixel-fire-effect
	 */
	
	private static boolean active = false;
	
	
	public static void stop() {
		active = false;
	}
	
	public static void start() {
		if(!active) {
			active = true;
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					int pixels = Main.getLedNum();
					
					while(active) {
						
						HashMap<Integer, Color> pixelHash = new HashMap<>();
						int r = 255, g = 95, b = 12;
						
						for(int i = 0; i < pixels; i++) {
							
							int flicker = new Random().nextInt(40);
							int r1 = r - flicker;
							int g1 = g - flicker;
							int b1 = b - flicker;
							
							if(r1 < 0) r1 = 0;
							if(g1 < 0) g1 = 0;
							if(b1 < 0) b1 = 0;
							
							pixelHash.put(i, new Color(r1, g1, b1));
						}
						
						Client.sendWS281xList(pixelHash);
						
						int delay = new Random().nextInt(100) + 50;
						
						try {
							Thread.sleep(delay);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
					}
					
				}
			}).start();
		}
	}

}
