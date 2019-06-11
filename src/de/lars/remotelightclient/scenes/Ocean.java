package de.lars.remotelightclient.scenes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.network.Client;
import de.lars.remotelightclient.network.Identifier;

public class Ocean {
	
	private static boolean active = false;
	private static Color[] ocean;
	
	
	public static void stop() {
		active = false;
	}
	
	public static void start() {
		if(!active) {
			active = true;
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					HashMap<Integer, Color> pixelHash = new HashMap<>();
					int count = 0, loops = 0, pixels = Main.getLedNum();
					boolean right = true;
					initOcean();
					
					for(int i = 0; i < pixels; i++) {
						pixelHash.put(i, ocean[count]);
						count++;
						if(count >= ocean.length)
							count = 0;
					}
					Client.sendWS281xList(pixelHash);
					
					while(active) {
						
						count++;
						if(count >= ocean.length) {
							count = 0;
							loops++;
							
							if(loops == 4) {
								if(right) right = false;
								else right = true;
							}
						}
						
						if(right) {
							
							Client.send(new String[] {Identifier.WS_SHIFT_RIGHT, 1+""});
							
							Client.send(new String[] {Identifier.WS_COLOR_PIXEL, 0+"",
									ocean[count].getRed()+"",
									ocean[count].getGreen()+"",
									ocean[count].getBlue()+""});
							
						} else {
							
							Client.send(new String[] {Identifier.WS_SHIFT_LEFT, 1+""});
							
							Client.send(new String[] {Identifier.WS_COLOR_PIXEL, (pixels - 1)+"",
									ocean[count].getRed()+"",
									ocean[count].getGreen()+"",
									ocean[count].getBlue()+""});
						}
						
						try {
							Thread.sleep(SceneHandler.DELAY);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		}
	}
	
	
	private static void initOcean() {
		List<Color> colors = new ArrayList<Color>();
		
		for(int i = 80; i < 256; i += 5)
			colors.add(new Color(0, 0, i));
		
		for(int i = 0; i < 256; i += 5)
			colors.add(new Color(0, i, 255));
		
		for(int i = 255; i > 80; i -= 5)
			colors.add(new Color(0, 255, i));
		
		for(int i = 255; i > 0; i -= 5)
			colors.add(new Color(0, i, 80));
		
		ocean = colors.toArray(new Color[colors.size()]);
	}

}
