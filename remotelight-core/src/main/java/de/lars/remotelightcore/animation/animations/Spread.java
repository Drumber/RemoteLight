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
import de.lars.remotelightcore.utils.color.ColorUtil;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class Spread extends Animation {
	
	private Random random;
	private Color[] strip;
	private int hueStep;

	public Spread() {
		super("Spread");
	}
	
	@Override
	public void onEnable(int pixels) {
		random = new Random();
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, pixels);
		hueStep = 0;
		super.onEnable(pixels);
	}
	
	@Override
	public Color[] onEffect() {
		float ranPos = (float) random.nextGaussian();
		int pos = (int) Math.round(ranPos * (strip.length / 6) + (strip.length / 2));	// average 'length / 2' and deviation 'length / 6'
		
		int randHue = hueStep + new Random().nextInt(30);
		if(randHue >= RainbowWheel.getRainbow().length) {
			randHue = randHue - RainbowWheel.getRainbow().length;
		}
		
		Color c = RainbowWheel.getRainbow()[randHue];
		if(pos > 0 && pos < strip.length) {
			strip[pos] = ColorUtil.mixColor(strip[pos], c);
		}
		
		if(++hueStep >= RainbowWheel.getRainbow().length) {
			hueStep = 0;
		}
		
		// fade all to black
		strip = ColorUtil.dimColorSimple(strip, 1);
		
		return strip;
	}

}
