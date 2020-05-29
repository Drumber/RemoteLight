package de.lars.remotelightcore.musicsync.modes;

import java.awt.Color;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.musicsync.MusicEffect;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.utils.color.ColorUtil;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;
import de.lars.remotelightcore.utils.maths.MathHelper;
import de.lars.remotelightcore.utils.maths.OpenSimplexNoise;

public class RainbowNoise extends MusicEffect {
	
	private OpenSimplexNoise noise;
	private Color[] strip;
	
	private float zoff = 0.0f;
	private float zincrement = 0.01f;
	private float xincrement = 0.002f;
	private float yincrement = 0.001f;
	
	private final int defaultBrightness = 80;
	private boolean brightnessTrigger = false;
	private float brightnessTime = 1f;
	
	public RainbowNoise() {
		super("RainbowNoise");
	}
	
	@Override
	public void onEnable() {
		noise = new OpenSimplexNoise();
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum());
		zoff = 0.0f;
		brightnessTime = 1f;
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		// reset x and y offset
		float xoff = 0.0f;
		float yoff = 0.0f;
		
		// get volume and peak info
		double vol = getSpl();
		boolean peak = isBump();
		
		// calculate multiplier
		float multiplier = (float) (vol * 0.05f * (getAdjustment() * 0.2));
		multiplier = Math.max(1, multiplier);
		
		// increase zoff
		zoff += zincrement * multiplier;
		
		// if volume peak -> increase zoff and multiplier
		if(peak) {
			zoff += 0.1f;
			multiplier += 0.05f;
		}
		
		if(!brightnessTrigger)
			brightnessTrigger = peak;

		if(brightnessTrigger) {
			brightnessTime -= 0.1f;
		}
		if(brightnessTime <= 0f) {
			brightnessTime = 1f;
			brightnessTrigger = false;
		}
		
		for(int i = 0; i < strip.length; i++) {
			xoff += xincrement * multiplier;
			yoff += yincrement * multiplier;
			
			// get noise value for hue
			float noiseHue = (float) noise.eval(xoff, yoff, zoff);
			int hue = (int) MathHelper.map(noiseHue, -1, 1, 0, RainbowWheel.getRainbow().length - 1);
			
			Color color = RainbowWheel.getRainbow()[hue];
			int bright = defaultBrightness;
			
			if(brightnessTrigger) {
				double halfStrip = strip.length / 2;
				double a = -brightnessTime;
				int brightness = (int) (a * Math.pow((i - halfStrip), 2) + (100 - defaultBrightness));
				if(brightness > 0) {
					bright += brightness;
				}
			}
			color = ColorUtil.dimColor(color, bright);
			strip[i] = color;
		}
		
		OutputManager.addToOutput(strip);
		super.onLoop();
	}

}
