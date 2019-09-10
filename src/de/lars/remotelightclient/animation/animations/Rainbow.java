package de.lars.remotelightclient.animation.animations;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.animation.Animation;
import de.lars.remotelightclient.utils.PixelColorUtils;
import de.lars.remotelightclient.utils.RainbowWheel;

public class Rainbow extends Animation {
	
	int step;

	public Rainbow() {
		super("Rainbow");
	}
	
	@Override
	public void onEnable() {
		step = 0;
		for(int i = 0; i < Main.getLedNum(); i++) {
			PixelColorUtils.shiftRight(1);
			
			step += 3;
			if(step >= RainbowWheel.getRainbow().length)
				step = 0;
			
			PixelColorUtils.setPixel(0, RainbowWheel.getRainbow()[step]);
		}
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		PixelColorUtils.shiftRight(1);
		
		step += 3;
		if(step >= RainbowWheel.getRainbow().length)
			step = 0;
		
		PixelColorUtils.setPixel(0, RainbowWheel.getRainbow()[step]);
		
		super.onLoop();
	}

}
