package de.lars.remotelightclient.arduino.animations;

import java.awt.Color;

import de.lars.remotelightclient.arduino.Arduino;

public class Wipe_Arduino {

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
					Color[] colors = {Color.RED, Color.ORANGE, Color.PINK, Color.MAGENTA,
				    		Color.BLUE, Color.CYAN, Color.GREEN};
					Color c = Color.RED;
					Color[] strip = Arduino.getStrip();
					int pix = 0, count = 0;
					boolean wiping = false;
					while(active) {
						if(!wiping) {
							count++;
							if(count >= colors.length) count = 0;
							c = colors[count];
							Arduino.setColorPixel(0, c);
							pix = 0;
							wiping = true;
						} else {
							pix++;
							if(pix < strip.length) {
								Arduino.setColorPixel(pix, c);
							} else {
								wiping = false;
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
