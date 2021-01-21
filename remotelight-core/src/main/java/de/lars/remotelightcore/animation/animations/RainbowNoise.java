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

import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingDouble;
import de.lars.remotelightcore.utils.color.Color;
import de.lars.remotelightcore.utils.color.ColorUtil;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;
import de.lars.remotelightcore.utils.maths.MathHelper;
import de.lars.remotelightcore.utils.maths.OpenSimplexNoise;

public class RainbowNoise extends Animation {
	
	private OpenSimplexNoise noiseBright, noiseColor;
	private Color[] strip;
	
	private float zoff = 0.0f;
	private float zincrement = 0.05f;

	public RainbowNoise() {
		super("RainbowNoise");
		// register settings
		this.addSetting(new SettingBoolean("animation.rainbownoise.brgightnesschange", "Brightness change", SettingCategory.Intern, "Vary the brightness", false));
		this.addSetting(new SettingDouble("animation.rainbownoise.xincrement", "x-Increment", SettingCategory.Intern, null, 0.02, 0, 5, 0.005));
		this.addSetting(new SettingDouble("animation.rainbownoise.yincrement", "y-Increment", SettingCategory.Intern, null, 0.005, 0, 5, 0.005));
		this.addSetting(new SettingDouble("animation.rainbownoise.timeincrement", "Time-Increment", SettingCategory.Intern, null, 0.02, 0, 5, 0.002));
	}
	
	@Override
	public void onEnable(int pixels) {
		// initialize strip color array
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, pixels);
		// initialize noise
		noiseBright = new OpenSimplexNoise();
		noiseColor = new OpenSimplexNoise();
		super.onEnable(pixels);
	}
	
	@Override
	public Color[] onEffect() {
		// get increment values from settings
		float xincrement = ((SettingDouble) getSetting("animation.rainbownoise.xincrement")).get().floatValue();
		float yincrement = ((SettingDouble) getSetting("animation.rainbownoise.yincrement")).get().floatValue();
		zincrement = ((SettingDouble) getSetting("animation.rainbownoise.timeincrement")).get().floatValue();
		
		zoff += zincrement;
		float xoff = 0.0f;
		float yoff = 0.0f;
		
		for(int x = 0; x < strip.length; x++) {
			// increase xoff and yoff
			xoff += xincrement;
			yoff += yincrement;
			
			// get brightness noise value
			float nB = (float) noiseBright.eval(xoff, yoff, zoff);
			int bright = (int) MathHelper.map(nB, -1, 1, 0, 100);
			
			// get hue noise value
			float nC = (float) noiseColor.eval(xoff, yoff, zoff);
			int hue = (int) MathHelper.map(nC, -1, 1, 0, RainbowWheel.getRainbow().length-1);
			
			Color color = RainbowWheel.getRainbow()[hue];
			
			// change brightness of color (if enabled in options)
			if(((SettingBoolean)getSetting("animation.rainbownoise.brgightnesschange")).get()) {
				color = ColorUtil.dimColor(color, bright);
			}
			
			// set color on position x to new color
			strip[x] = color;
		}
		
		// show the strip
		return strip;
	}

}
