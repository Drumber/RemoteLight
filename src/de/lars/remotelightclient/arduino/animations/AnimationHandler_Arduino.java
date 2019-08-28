package de.lars.remotelightclient.arduino.animations;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.network.Client;
import de.lars.remotelightclient.network.Identifier;

public class AnimationHandler_Arduino {
	
	private static boolean active = false;
	private static int speed = 50;
	
	
	public static void stop() {
		Main.getInstance().getAnimationManager().stop();
		
		/*Rainbow_Arduino.stop();
		RunningLight_Arduino.stop();
		Scan_Arduino.stop();
		Wipe_Arduino.stop();
		
		Client.send(new String[] {Identifier.WS_COLOR_OFF});
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		active = false;
	}
	
	public static boolean isActive() {
		return active;
	}
	
	public static void setSpeed(int speed) {
		/*AnimationHandler_Arduino.speed = speed;
		Rainbow_Arduino.setSpeed(speed);
		RunningLight_Arduino.setSpeed(speed);
		Scan_Arduino.setSpeed(speed);
		Wipe_Arduino.setSpeed(speed);*/
		
	}

	public static void startRainbow() {
		stop();
		//Rainbow_Arduino.start(speed);
		active = true;
	}
	
	public static void startRunningLight() {
		stop();
		//RunningLight_Arduino.start(speed);
		active = true;
	}
	
	public static void startScan() {
		stop();
		//Scan_Arduino.start(speed);
		active = true;
	}
	
	public static void startWipe() {
		stop();
		//Wipe_Arduino.start(speed);
		active = true;
	}
	
}
