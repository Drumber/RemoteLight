package de.lars.remotelightclient.animation.animations;

import java.awt.Color;
import java.util.Random;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.animation.Animation;
import de.lars.remotelightclient.utils.PixelColorUtils;
import de.lars.remotelightclient.utils.RainbowWheel;

public class RandomColor2 extends Animation {

	public RandomColor2() {
		super("RandomColor#2");
	}
	
	@Override
	public void onLoop() {
		int rnd = new Random().nextInt(Main.getLedNum());
		
		PixelColorUtils.setPixel(rnd, rndmColor());
		
		super.onLoop();
	}
	
	private Color rndmColor() {
		return RainbowWheel.getRainbow()[new Random().nextInt(RainbowWheel.getRainbow().length)];
	}

}
