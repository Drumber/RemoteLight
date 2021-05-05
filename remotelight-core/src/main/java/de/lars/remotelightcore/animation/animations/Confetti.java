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

import java.util.Random;

import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.utils.color.Color;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;

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
	public void onEnable(int pixels) {
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, pixels);
		hue = 0;
		super.onEnable(pixels);
	}
	
	@Override
	public Color[] onEffect() {
		// fade all pixels to black
		dim();
		Color c = RainbowWheel.getRainbow()[hue + new Random().nextInt(40)];
		
		int loopsAmount = Math.max(1, strip.length / 60);
		for(int i = 0; i < loopsAmount; i++) {
			int pos = new Random().nextInt(strip.length);
			strip[pos] = c;
		}
		
		if(++hue >= RainbowWheel.getRainbow().length - 40)
			hue = 0;
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
