/*-
 * >===license-start
 * RemoteLight
 * ===
 * Copyright (C) 2019 - 2020 Drumber
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

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class Open extends Animation {
	
	private Color[] strip;
	private int pos;
	private Color color;
	private boolean fadeOut;

	public Open() {
		super("Open");
		color = RainbowWheel.getRandomColor();
		this.addSetting(new SettingBoolean("animation.open.randomcolor", "Random color", SettingCategory.Intern, null, true));
		this.addSetting(new SettingColor("animation.open.color", "Color", SettingCategory.Intern,	null, Color.RED));
		this.addSetting(new SettingBoolean("animation.open.fadeout", "Fade out", SettingCategory.Intern, null, true));
	}
	
	@Override
	public void onEnable() {
		fadeOut = false;
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum());
		pos = strip.length / 2;
		color = RainbowWheel.getRandomColor();
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		int half = strip.length / 2;
		
		if(!((SettingBoolean) getSetting("animation.open.randomcolor")).getValue()) {
			color = ((SettingColor) getSetting("animation.open.color")).getValue();
		}
		
		strip[pos] = color;
		for(int i = 0; i < half; i++) {
			if(i <= pos) {
				if(fadeOut)
					strip[i] = Color.BLACK;
				else
					strip[i] = color;
			} else {
				if(fadeOut)
					strip[i] = color;
				else
					strip[i] = Color.BLACK;
			}
		}
		
		if(--pos == 0) {
			pos = half;
			if(((SettingBoolean) getSetting("animation.open.randomcolor")).getValue() && !fadeOut) {
				color = RainbowWheel.getRandomColor();
			}
			if(((SettingBoolean) getSetting("animation.open.fadeout")).getValue()) {
				fadeOut = !fadeOut;
			} else {
				fadeOut = false;
			}
		}
		
		strip = PixelColorUtils.centered(strip, false);
		
		OutputManager.addToOutput(strip);
		super.onLoop();
	}
	
}
