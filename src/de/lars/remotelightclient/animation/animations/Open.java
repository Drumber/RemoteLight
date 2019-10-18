package de.lars.remotelightclient.animation.animations;

import java.awt.Color;
import java.util.Random;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.animation.Animation;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.utils.PixelColorUtils;
import de.lars.remotelightclient.utils.RainbowWheel;

public class Open extends Animation {
	
	private int pos;
	private Color color;

	public Open() {
		super("Open");
		color = rndmColor();
	}
	
	@Override
	public void onLoop() {
		int half = Main.getLedNum() / 2;
		Color[] strip = Main.getInstance().getOutputManager().getLastColors();
		
		strip[pos] = color;
		for(int i = 0; i < half; i++) {
			if(i < pos) {
				strip[i] = color;
			} else {
				strip[i] = Color.BLACK;
			}
		}
		
		if(pos-- == 0) {
			pos = half;
			color = rndmColor();
			strip = PixelColorUtils.colorAllPixels(Color.BLACK, strip.length);
			strip[0] = color;
		}
		
		strip = PixelColorUtils.centered(strip, false);
		
		OutputManager.addToOutput(strip);
		super.onLoop();
	}
	
	private Color rndmColor() {
		return RainbowWheel.getRainbow()[new Random().nextInt(RainbowWheel.getRainbow().length)];
	}

}
