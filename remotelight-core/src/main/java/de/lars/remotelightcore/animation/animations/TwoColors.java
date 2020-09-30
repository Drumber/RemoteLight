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

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class TwoColors extends Animation {
	
	private int colorCounter = 0;
	private boolean color1 = true;

	public TwoColors() {
		super("Two Colors");
		this.addSetting(new SettingColor("animation.twocolors.color1", "Color 1", SettingCategory.Intern,	null, Color.RED));
		this.addSetting(new SettingColor("animation.twocolors.color2", "Color 2", SettingCategory.Intern,	null, Color.GREEN));
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
		PixelColorUtils.shiftRight(1);
		
		if(++colorCounter == 2) {
			colorCounter = 0;
			color1 = !color1;
		}
		
		Color c;
		if(color1) {
			c =((SettingColor) getSetting("animation.twocolors.color1")).get();
		} else {
			c = ((SettingColor) getSetting("animation.twocolors.color2")).get();
		}
		
		PixelColorUtils.setPixel(0, c);
		super.onLoop();
	}

}
