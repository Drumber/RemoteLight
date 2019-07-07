package de.lars.remotelightclient.arduino.animations;

import java.awt.Color;

import de.lars.remotelightclient.arduino.Arduino;

public class RunningLight_Arduino {

	private static int speed;
	private static boolean active;
	private static boolean sym;
	
	public static void stop() {
		active = false;
	}
	
	public static void setSpeed(int delay) {
		speed = delay;
	}
	
	public static void setSymmetric(boolean symmetric) {
		sym = symmetric;
	}
	
	public static void start(int delay) {
		if(!active) {
			speed = delay;
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					active = true;
				    Color[] color = {Color.RED, Color.ORANGE, Color.PINK, Color.MAGENTA,
				    		Color.BLUE, Color.CYAN, Color.GREEN};
				    
				    int pass = 0, counter = 0;
					while(active) {
						if(!sym) {
							if(pass < 5) {
								switch (pass) {
								case 0:
									if(counter >= color.length) counter = 0;
									Arduino.setColorPixel(0, color[counter].darker().darker());
									break;
								case 1:
									if(counter > color.length) counter = 0;
									Arduino.setColorPixel(0, color[counter].darker());
									break;
								case 2:
									if(counter > color.length) counter = 0;
									Arduino.setColorPixel(0, color[counter]);
									break;
								case 3:
									if(counter > color.length) counter = 0;
									Arduino.setColorPixel(0, color[counter].darker());
									break;
								case 4:
									Arduino.setColorPixel(0, color[counter].darker().darker());
									counter++;
									if(counter >= color.length) counter = 0;
									break;
									
								default:
									break;
								}
								
								pass++;
							} else if(pass < 10) {
								pass++;
								Arduino.setColorPixel(0, Color.BLACK);
							} else {
								pass = 0;
								Arduino.setColorPixel(0, Color.BLACK);
							}
							
							//shift pixels to right
							Arduino.shiftRight(1);
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
