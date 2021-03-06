/*-
 * >===license-start
 * RemoteLight
 * ===
 * Copyright (C) 2019 - 2020 Lars O.
 * ===
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * <===license-end
 */

package de.lars.remotelightcore.animation.animations;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.utils.color.Color;
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
	public void onEnable(int pixels) {
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, pixels);
		hue = 0;
		super.onEnable(pixels);
	}
	
	@Override
	public Color[] onEffect() {
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
		
		return strip;
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
