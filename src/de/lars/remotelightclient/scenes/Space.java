package de.lars.remotelightclient.scenes;

import java.util.HashMap;
import java.util.Random;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.network.Client;
import de.lars.remotelightclient.network.Identifier;

public class Space {
	
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
					
					int pix = Main.getLedNum();
					HashMap<Integer, Integer> stars = new HashMap<>(); //ledNum, Brightness
					Random r = new Random();
					
					while(active) {
						
						if(stars.size() < ((pix / 4) + r.nextInt(10))) {
							int led = r.nextInt(pix);
							int brightness = r.nextInt(200) + 56;
							
							stars.put(led, brightness);
						}
						
						for(int i = 0; i < pix; i++) {
							if(stars.containsKey(i)) {
								
								int b = stars.get(i);
								Client.send(new String[] {Identifier.WS_COLOR_PIXEL, i+"", b+"", b+"", (b / 2)+""});
								
								b -= 5;
								if(b <= 0) {
									Client.send(new String[] {Identifier.WS_COLOR_PIXEL, i+"", 0+"", 0+"", 0+""});
									stars.remove(i);
								} else {
									stars.put(i, b);
								}
							}
						}
						
						
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		}
	}

}
