package de.lars.remotelightclient.scenes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.network.Client;
import de.lars.remotelightclient.scene.scenes.NorthernLightTrail;

public class NorthernLights {
	
	private static boolean active = false;
	private static Color[] colors = {new Color(0, 207, 82), new Color(3, 46, 62), new Color(25, 100, 106), new Color(0, 198, 144),
			new Color(0, 223, 150), new Color(142, 0, 251)};
	
	
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
					int count = 0;
					
					Color[] strip = new Color[pix];
					strip = initStrip(strip);
					
					Random r = new Random();
					boolean newColor = true;
					HashMap<Integer, NorthernLightTrail> lights = new HashMap<>();
					
					while(active) {
						
						count = lights.size();
						
						if(count < (pix / 20))
							newColor = true;
						else
							newColor = false;
						
						if(newColor) {
							int ranColor = r.nextInt(colors.length);
							int startPix = r.nextInt(pix);
							int direction = r.nextInt(2);
							
							boolean right;
							
							if(direction == 0) right = true;
							else right = false;
							
							
							lights.put(startPix, new NorthernLightTrail(startPix, colors[ranColor], right));
						}
						
						//dim all
						for(int i = 0; i < pix; i++) {
							int red = strip[i].getRed() - (strip[i].getRed() / 8);
							int green = strip[i].getGreen() - (strip[i].getGreen() / 8);
							int blue = strip[i].getBlue() - (strip[i].getBlue() / 8);
							
							if(red < 10 && green < 10 && blue < 10)
								strip[i] = Color.BLACK;
							else
								strip[i] = new Color(red, green, blue);
						}
						
						//set pixel color
						HashMap<Integer, Color> pixelHash = new HashMap<>();
						List<Integer> alreadyPut = new ArrayList<>();
						
						for(int i = 0; i < pix; i++) {
							
							if(lights.containsKey(i) && !alreadyPut.contains(i)) {
								
								NorthernLightTrail led = lights.get(i);
								
								pixelHash.put(i, led.color);
								strip[i] = led.color;
								
								if(led.right) {
									
									if(i != (pix - 1)) {
										lights.put((i + 1), led);
										alreadyPut.add((i + 1));
										
									}
									
									lights.remove(i);
									
								} else {
									
									if(i != 0) {
										lights.put((i - 1), led);
										alreadyPut.add((i - 1));
										
									}
									
									lights.remove(i);
								}
							} else {
								pixelHash.put(i, strip[i]);
							}
						}
						
						Client.sendWS281xList(pixelHash);
						
						try {
							Thread.sleep(r.nextInt(100) + 50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
				}
			}).start();
		}
	}
	
	
	private static Color[] initStrip(Color[] strip) {
		for(int i = 0; i < strip.length; i++)
			strip[i] = Color.BLACK;
		return strip;
	}

}
