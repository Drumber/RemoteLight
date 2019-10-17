package de.lars.remotelightclient.animation.animations;

import java.awt.Color;
import java.util.Random;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.animation.Animation;
import de.lars.remotelightclient.utils.PixelColorUtils;
import de.lars.remotelightclient.utils.RainbowWheel;

public class RandomColor extends Animation {
	
	private int pos;

	public RandomColor() {
		super("RandomColor#1");
	}
	
	@Override
	public void onLoop() {
		PixelColorUtils.setPixel(pos, rndmColor());
		
		if(++pos >= Main.getLedNum()) {
			pos = 0;
		}
		super.onLoop();
	}
	
	private Color rndmColor() {
		return RainbowWheel.getRainbow()[new Random().nextInt(RainbowWheel.getRainbow().length)];
	}

}
