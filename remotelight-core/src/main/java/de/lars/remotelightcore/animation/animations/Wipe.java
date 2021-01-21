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
import de.lars.remotelightcore.utils.color.ColorUtil;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class Wipe extends Animation {
	
	private Color[] strip;
	private int pos;
	private Color color;

	public Wipe() {
		super("Wipe");
		this.addSetting(new SettingBoolean("animation.wipe.randomcolor", "Random color", SettingCategory.Intern, null, true));
		this.addSetting(new SettingColor("animation.wipe.color", "Color", SettingCategory.Intern,	null, Color.RED));
	}
	
	@Override
	public void onEnable(int pixels) {
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, pixels);
		pos = 0;
		color = RainbowWheel.getRandomColor();
		if(!((SettingBoolean) getSetting("animation.wipe.randomcolor")).get()) {
			color = ((SettingColor) getSetting("animation.wipe.color")).get();
		}
		super.onEnable(pixels);
	}
	
	@Override
	public Color[] onEffect() {
		strip[pos] = color;
		
		if(++pos >= strip.length - 1) {
			pos = 0;
			
			if(!((SettingBoolean) getSetting("animation.wipe.randomcolor")).get()) {
				if(color == Color.BLACK) {
					color = ((SettingColor) getSetting("animation.wipe.color")).get();
				} else {
					color = Color.BLACK;
				}
			} else {
				color = getRandomColor();
			}
		}
		return strip;
	}
	
	private Color getRandomColor() {
		Color c = Color.RED;
		do {
			c = RainbowWheel.getRandomColor();
		} while(ColorUtil.similar(c, color));
		return c;
	}
	
	@Override
	public void onSettingUpdate() {
		this.hideSetting("animation.wipe.color", getSetting(SettingBoolean.class, "animation.wipe.randomcolor").get());
		super.onSettingUpdate();
	}

}
