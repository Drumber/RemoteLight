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
import java.math.BigDecimal;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.utils.color.ColorUtil;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class ColorWave extends Animation {

	private Color color, oldColor, newColor;
	private double step = 1.0;
	private BigDecimal fade = new BigDecimal("0.0");
	
	public ColorWave() {
		super("ColorWave");
		color = RainbowWheel.getRandomColor();
		oldColor = color;
		newColor = RainbowWheel.getRandomColor();
		this.addSetting(new SettingBoolean("animation.colorwave.randomcolor", "Random color", SettingCategory.Intern, null, false));
		this.addSetting(new SettingColor("animation.colorwave.color", "Color", SettingCategory.Intern,	null, Color.RED));
		this.addSetting(new SettingBoolean("animation.colorwave.wave", "Wave", SettingCategory.Intern, "Increase and decrease brightness dynamically", true));
	}
	
	
	@Override
	public void onEnable() {
		for(int i = 0; i < RemoteLightCore.getLedNum(); i++) {
			onLoop();
		}
		super.onEnable();
	}
	
	
	@Override
	public void onLoop() {
		if(!((SettingBoolean) getSetting("animation.colorwave.randomcolor")).getValue()) {
			color = ((SettingColor) getSetting("animation.colorwave.color")).getValue();
		}
		
		if(!((SettingBoolean) getSetting("animation.colorwave.randomcolor")).getValue()) {
			color = ((SettingColor) getSetting("animation.colorwave.color")).getValue();
		}
		
		PixelColorUtils.shiftRight(1);
		PixelColorUtils.setPixel(0, calcWave(step, color));
		
		step += 0.01;
		if(step >= 2.0) {
			step = 0;
		}
		
		// fade to new color
		color = ColorUtil.fadeToColor(oldColor, newColor, fade.floatValue());
		
		fade = fade.add(new BigDecimal("0.01"));
		if(fade.floatValue() > 1.0) {
			fade = new BigDecimal("0.0");
			oldColor = newColor;
			newColor = RainbowWheel.getRandomColor();
		}
		super.onLoop();
	}
	
	private Color calcWave(double stepping, Color c) {
		int dim = (int) (45 * Math.sin(Math.PI * stepping) + 5);
		if(!((SettingBoolean) getSetting("animation.colorwave.wave")).getValue()) {
			return c;
		}
		return ColorUtil.dimColor(c, dim);
	}

}
