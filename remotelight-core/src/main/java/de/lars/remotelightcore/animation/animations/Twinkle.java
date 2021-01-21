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

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.animation.AnimationManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.utils.color.Color;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.maths.TimeUtil;

public class Twinkle extends Animation {
	
	private AnimationManager am;
	private Color[] strip;
	private int max;
	private Color color;
	private TimeUtil time;

	public Twinkle() {
		super("Twinkle");
		this.addSetting(new SettingColor("animation.twinkle.color", "Color", SettingCategory.Intern,	null, new Color(255, 240, 255)));
	}
	
	@Override
	public void onEnable(int pixels) {
		am = RemoteLightCore.getInstance().getAnimationManager();
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, pixels);
		max = pixels / 10;
		time = new TimeUtil(am.getDelay());
		super.onEnable(pixels);
	}
	
	@Override
	public Color[] onEffect() {
		if(time.hasReached()) {
			strip = PixelColorUtils.colorAllPixels(Color.BLACK, getPixel());
			color = ((SettingColor) getSetting("animation.twinkle.color")).get();
			
			for(int i = 0; i <= max; i++) {
				if(new Random().nextInt(3) == 0) {
					int pos = new Random().nextInt(strip.length);
					strip[pos] = color;
				}
			}
			
			int rnd = (am.getDelay() * 2) + new Random().nextInt(50);
			time.setInterval(rnd);
		}
		return strip;
	}

}
