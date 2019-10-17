package de.lars.remotelightclient.animation.animations;

import java.awt.Color;
import java.util.Random;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.animation.Animation;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.utils.PixelColorUtils;
import de.lars.remotelightclient.utils.RainbowWheel;
import de.lars.remotelightclient.utils.TimeUtil;

public class Jump extends Animation {
	
	private TimeUtil time;

	public Jump() {
		super("Jump");
	}
	
	@Override
	public void onEnable() {
		time = new TimeUtil(Main.getInstance().getAnimationManager().getDelay()*3);
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		time.setInterval(Main.getInstance().getAnimationManager().getDelay()*3);
		
		if(time.hasReached()) {
			Color color = RainbowWheel.getRainbow()[new Random().nextInt(RainbowWheel.getRainbow().length)];
			OutputManager.addToOutput(PixelColorUtils.colorAllPixels(color, Main.getLedNum()));
		}
		
		super.onLoop();
	}

}
