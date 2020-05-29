package de.lars.remotelightcore.animation.animations;

import java.awt.Color;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;
import de.lars.remotelightcore.utils.maths.MathHelper;

public class Sinelon extends Animation {
	/* https://gist.github.com/kriegsman/062e10f7f07ba8518af6
	 * by Mark Kriegsman
	 */
	
	private Color[] strip;
	private int hue;
	private int bpm;

	public Sinelon() {
		super("Sinelon");
	}
	
	@Override
	public void onEnable() {
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum());
		hue = 0;
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		// set the BPM depending on the set speed
		bpm = 800 / RemoteLightCore.getInstance().getAnimationManager().getDelay();
		if(bpm > 127) bpm = 127;
		if(bpm < 1) bpm = 1;
		
		// get position
		int pos = MathHelper.beatsin16(bpm, strip.length, 0, 0.01);
		
		// t must be between 0 and 1
		float t = (float) pos / (strip.length - 1);
		// easing -> slow at the ends and faster in the middle of the strip
		float ease = MathHelper.easeInOutQuadSimple(t);
		// map ease value (0..1) to the length of the strip
		int mapped = (int) MathHelper.lerp(0, strip.length-1, ease);
		pos = mapped;
		
		// fade all pixels to black
		dim();
		
		strip[pos] = RainbowWheel.getRainbow()[hue];
		
		if(++hue >= RainbowWheel.getRainbow().length) {
			hue = 0;
		}
		
		OutputManager.addToOutput(strip);
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
