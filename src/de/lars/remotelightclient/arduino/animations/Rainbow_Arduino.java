package de.lars.remotelightclient.arduino.animations;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.arduino.Arduino;
import de.lars.remotelightclient.arduino.RainbowWheel;


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
	    
		speed = delay;
		if(!active) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					active = true;
					
					int step = 0;
					for(int i = 0; i < Main.getLedNum(); i++) {
						Arduino.setColorPixel(i, RainbowWheel.getRainbow()[step]);
						step += 5;
						if(step >= RainbowWheel.getRainbow().length)
							step = 0;
					}
					
					while(active) {
						Arduino.shiftRight(1);
						Arduino.setColorPixel(0, RainbowWheel.getRainbow()[step]);
						step += 3;
						if(step >= RainbowWheel.getRainbow().length)
							step = 0;
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
