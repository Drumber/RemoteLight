package de.lars.remotelightclient.arduino.animations;

import java.awt.Color;
import java.util.Random;

import de.lars.remotelightclient.arduino.Arduino;

public class Scan_Arduino {

	private static int speed;
	private static boolean active;
	
	public static void stop() {
		active = false;
	}
	
	public static void setSpeed(int delay) {
		speed = delay;
	}
	
	public static void start(int delay) {
		if(!active) {
			speed = delay;
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					active = true;
					Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.PINK, Color.MAGENTA,
				    		Color.BLUE, Color.CYAN, Color.GREEN};
					Color c = Color.RED;
					Color[] strip = Arduino.getStrip();
					int pix = 0;
					boolean scanning = false, reverse = false;
					while(active) {
						if(!scanning) {
							int r = new Random().nextInt(colors.length);
							c = colors[r];
							Arduino.setColorPixel(0, c);
							pix = 0;
							scanning = true;
						} else {
							Arduino.setColorPixel(pix, Color.BLACK);
							if(!reverse) {
								pix++;
								if(pix < strip.length) {
									Arduino.setColorPixel(pix, c);
								} else {
									reverse = true;
									pix--;
									Arduino.setColorPixel(pix, c);
								}
							} else {
								pix--;
								if(pix > 0) {
									Arduino.setColorPixel(pix, c);
								} else {
									reverse = false;
									scanning = false;
									Arduino.setColorPixel(pix, c);
								}
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
