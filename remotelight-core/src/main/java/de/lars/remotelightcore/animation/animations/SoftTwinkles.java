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

import java.awt.Color;
import java.util.Random;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class SoftTwinkles extends Animation {
	/* adapted from https://gist.github.com/kriegsman/99082f66a726bdff7776
	 * by Mark Kriegsman
	 */
	
	private int DENSITY = 80;
	private final int red = 8, green = 7, blue = 1;
	private Color[] strip;

	public SoftTwinkles() {
		super("SoftTwinkles");
		this.addSetting(new SettingInt("animation.softtwinkles.density", "Density", SettingCategory.Intern, null, 80, 5, 255, 5));
	}
	
	@Override
	public void onEnable() {
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum());
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		DENSITY = ((SettingInt) getSetting("animation.softtwinkles.density")).getValue();
		
		for(int i = 0; i < strip.length; i++) {
			if(isBlack(strip[i])) continue;
			if(strip[i].getRed() % 2 != 0) {	// dim if red is odd
				strip[i] = dim(strip[i], -1);
			} else {							// else brighten
				strip[i] = dim(strip[i], +1);
			}
		}
		
		if(new Random().nextInt(255) < DENSITY) {
			int pos = new Random().nextInt(strip.length);
			if(isBlack(strip[pos])) {
				strip[pos] = dim(strip[pos], +1);
			}
		}
		
		OutputManager.addToOutput(strip);
		super.onLoop();
	}
	
	private boolean isBlack(Color c) {
		return c.getRed() == 0 && c.getGreen() == 0 && c.getBlue() == 0;
	}
	
	private Color dim(Color c, int factor) {
		int r = c.getRed() + (red * factor);
		int g = c.getGreen() + (green * factor);
		int b = c.getBlue() + (blue * factor);
		
		if(r < 0) r = 0;
		if(g < 0) g = 0;
		if(b < 0) b = 0;
		
		if(r > 255) r = 255;
		if(g > 255) g = 255;
		if(b > 255) b = 255;
		
		return new Color(r, g, b);
	}

}
