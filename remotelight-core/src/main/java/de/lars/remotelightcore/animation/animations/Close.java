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
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.utils.color.Color;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class Close extends Animation {
	
	private Color[] strip;
	private int pos;
	private Color color;
	private boolean fadeToBlack;

	public Close() {
		super("Close");
		color = RainbowWheel.getRandomColor();
		this.addSetting(new SettingBoolean("animation.close.randomcolor", "Random color", SettingCategory.Intern, null, true));
		this.addSetting(new SettingColor("animation.close.color", "Color", SettingCategory.Intern, null, Color.RED));
		this.addSetting(new SettingBoolean("animation.close.fadeout", "Fade out", SettingCategory.Intern, null, true));
	}
	
	@Override
	public void onEnable(int pixels) {
		fadeToBlack = false;
		pos = 0;
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, pixels);
		color = RainbowWheel.getRandomColor();
		super.onEnable(pixels);
	}
	
	@Override
	public Color[] onEffect() {
		int half = strip.length / 2;
		
		if(!((SettingBoolean) getSetting("animation.close.randomcolor")).get() && !fadeToBlack) {
			color = ((SettingColor) getSetting("animation.close.color")).get();
		}
		
		strip[pos] = color;
		for(int i = 0; i < half; i++) {
			if(i <= pos) {
				strip[i] = color;
			}
		}
		
		if(pos++ == half) {
			pos = 0;
			fadeToBlack = !fadeToBlack;
			color = RainbowWheel.getRandomColor();
			if(!((SettingBoolean) getSetting("animation.close.fadeout")).get()) {
				strip = PixelColorUtils.colorAllPixels(Color.BLACK, strip.length);
			} else if(fadeToBlack) {
				color = Color.BLACK;
			}
		}
		
		strip = PixelColorUtils.centered(strip, false);
		return strip;
	}
	
	@Override
	public void onSettingUpdate() {
		this.hideSetting("animation.close.color", getSetting(SettingBoolean.class, "animation.close.randomcolor").get());
		super.onSettingUpdate();
	}

}
