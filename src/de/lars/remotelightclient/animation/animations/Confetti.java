package de.lars.remotelightclient.animation.animations;

import java.awt.Color;
import java.util.Random;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.animation.Animation;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.utils.PixelColorUtils;
import de.lars.remotelightclient.utils.RainbowWheel;

public class Confetti extends Animation {
	/* adapted from https://gist.github.com/kriegsman/062e10f7f07ba8518af6
	 * by Mark Kriegsman
	 */
	
	private Color[] strip;
	private int hue;

	public Confetti() {
		super("Confetti");
		
	}
	
	@Override
	public void onEnable() {
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, Main.getLedNum());
		hue = 0;
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		// fade all pixels to black
		dim();
		Color c = RainbowWheel.getRainbow()[hue + new Random().nextInt(40)];
		
		int loopsAmount = Main.getLedNum() / 60;
		for(int i = 0; i < loopsAmount; i++) {
			int pos = new Random().nextInt(strip.length);
			strip[pos] = c;
		}
		
		OutputManager.addToOutput(strip);
		if(++hue >= RainbowWheel.getRainbow().length - 40)
			hue = 0;
		super.onLoop();
	}
	
	private void dim() {
		for(int i = 0; i < strip.length; i++) {
			Color c = strip[i];
			int r = c.getRed() - 10;
			int g = c.getGreen() - 10;
			int b = c.getBlue() - 10;
			if(r < 0) r = 0;
			if(g < 0) g = 0;
			if(b < 0) b = 0;
			strip[i] = new Color(r, g, b);
		}
	}

}
