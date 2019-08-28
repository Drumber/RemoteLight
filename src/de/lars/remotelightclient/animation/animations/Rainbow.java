package de.lars.remotelightclient.animation.animations;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.animation.Animation;
import de.lars.remotelightclient.arduino.Arduino;
import de.lars.remotelightclient.arduino.RainbowWheel;

public class Rainbow extends Animation {
	
	int step;

	public Rainbow() {
		super("Rainbow");
	}
	
	@Override
	public void onEnable() {
		step = 0;
		for(int i = 0; i < Main.getLedNum(); i++) {
			Arduino.shiftRight(1);
			
			step += 3;
			if(step >= RainbowWheel.getRainbow().length)
				step = 0;
			
			Arduino.setColorPixel(0, RainbowWheel.getRainbow()[step]);
		}
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		Arduino.shiftRight(1);
		
		step += 3;
		if(step >= RainbowWheel.getRainbow().length)
			step = 0;
		
		Arduino.setColorPixel(0, RainbowWheel.getRainbow()[step]);
		
		super.onLoop();
	}

}
