package de.lars.remotelightclient.arduino.animations;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import de.lars.remotelightclient.arduino.Arduino;


public class Rainbow_Arduino {
	
	private static int speed;
	private static boolean active;
	
	public static void stop() {
		active = false;
	}
	
	public static void setSpeed(int speed) {
		Rainbow_Arduino.speed = speed;
	}
	
	
	public static void start(int delay) {
		List<Color> colors = new ArrayList<Color>();
	    for (int r=0; r<100; r++) colors.add(new Color(r*255/100,       255,         0));
	    for (int g=100; g>0; g--) colors.add(new Color(      255, g*255/100,         0));
	    for (int b=0; b<100; b++) colors.add(new Color(      255,         0, b*255/100));
	    for (int r=100; r>0; r--) colors.add(new Color(r*255/100,         0,       255));
	    for (int g=0; g<100; g++) colors.add(new Color(        0, g*255/100,       255));
	    for (int b=100; b>0; b--) colors.add(new Color(        0,       255, b*255/100));
	    colors.add(new Color(        0,       255,         0));
	    Color[] color = colors.toArray(new Color[colors.size()]);
	    
		speed = delay;
		if(!active) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					active = true;
					Color[] strip = Arduino.getStrip();
					int count = 0;
					for(int i = 0; i < strip.length; i++) {
						count += 8;
						if(count > color.length) count = 0;
						Color c = color[count];
						Arduino.setColorPixel(i, c.getRed(), c.getGreen(), c.getBlue());
					}
					
					while(active) {
						for(int i = 1; i <= strip.length; i++) {
							if(i == strip.length) {
								count += 8;
								if(count > color.length) count = 0;
								Color c = color[count];
								Arduino.setColorPixel(0, c.getRed(), c.getGreen(), c.getBlue());
							} else {
								Arduino.setColorPixel(strip.length - i, Arduino.getPixelColor(strip.length - i - 1));
							}
						}
						try {
							Thread.sleep(speed);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		}
	}

}
