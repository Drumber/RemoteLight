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
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.utils.color.Color;
import de.lars.remotelightcore.utils.color.ColorUtil;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class ColorWave extends Animation {

	private Color[] strip;
	private Color color, oldColor, newColor;
	private float step = 0.0f;
	private float fade = 0.0f;
	
	public ColorWave() {
		super("ColorWave");
		color = RainbowWheel.getRandomColor();
		oldColor = color;
		newColor = RainbowWheel.getRandomColor();
		this.addSetting(new SettingBoolean("animation.colorwave.randomcolor", "Random color", SettingCategory.Intern, null, false));
		this.addSetting(new SettingColor("animation.colorwave.color", "Color", SettingCategory.Intern,	null, Color.RED));
		this.addSetting(new SettingBoolean("animation.colorwave.wave", "Wave", SettingCategory.Intern, "Increase and decrease brightness dynamically", true));
		this.addSetting(new SettingInt("animation.colorwave.waveperiod", "Wave period", SettingCategory.Intern, null, 5, 1, 10, 1));
		this.addSetting(new SettingColor("animation.colorwave.wavebackground", "Background", SettingCategory.Intern, null, Color.BLACK));
	}
	
	
	@Override
	public void onEnable(int pixels) {
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, pixels);
		for(int i = 0; i < pixels; i++) {
			onEffect();
		}
		super.onEnable(pixels);
	}
	
	
	@Override
	public Color[] onEffect() {
		if(!((SettingBoolean) getSetting("animation.colorwave.randomcolor")).get()) {
			color = ((SettingColor) getSetting("animation.colorwave.color")).get();
		}
		
		if(!((SettingBoolean) getSetting("animation.colorwave.randomcolor")).get()) {
			color = ((SettingColor) getSetting("animation.colorwave.color")).get();
		}
		
		strip = PixelColorUtils.shiftPixelsRight(strip, 1);
		strip[0] = calcWave(color);
		
		// fade to new color
		color = ColorUtil.fadeToColor(oldColor, newColor, fade);
		
		fade += 0.01f;
		if(fade > 1.0) {
			fade = 0.0f;
			oldColor = newColor;
			newColor = RainbowWheel.getRandomColor();
		}
		return strip;
	}
	
	private Color calcWave(Color c) {
		if(!((SettingBoolean) getSetting("animation.colorwave.wave")).get()) {
			return c;
		}
		// f(x) = 50 * sin(b * PI * x) + 50
		// size = b
		float size = 1.0f / 10.0f * getSetting(SettingInt.class, "animation.colorwave.waveperiod").get();
		int dim = (int) (50 * Math.sin(size * Math.PI * step) + 50);
		
		Color background = getSetting(SettingColor.class, "animation.colorwave.wavebackground").get();
		float weight = dim / 100.0f;
		
		// sinus period = (2 * PI) / (b * PI) = 2 / b
		double period = 2.0f / size;
		// increment step counter
		step += 0.01f;
		if(step >= period) {
			// reset counter if period is reached
			step = 0;
		}
		
		return ColorUtil.mixColor(c, weight, background, 1.0f - weight);
	}
	
	@Override
	public void onSettingUpdate() {
		this.hideSetting("animation.colorwave.color", getSetting(SettingBoolean.class, "animation.colorwave.randomcolor").get());
		boolean isWave = getSetting(SettingBoolean.class, "animation.colorwave.wave").get();
		this.hideSetting("animation.colorwave.wavebackground", !isWave);
		this.hideSetting("animation.colorwave.waveperiod", !isWave);
		super.onSettingUpdate();
	}

}
