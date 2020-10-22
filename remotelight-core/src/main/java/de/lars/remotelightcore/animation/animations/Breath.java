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

import de.lars.remotelightcore.utils.color.Color;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.utils.color.ColorUtil;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class Breath extends Animation {
	
	private int brghtns = 100;
	private Color color;
	private boolean darker = true;
	private int nxtColorCounter = 0;

	public Breath() {
		super("Breath");
		color = RainbowWheel.getRandomColor();
		this.addSetting(new SettingBoolean("animation.breath.randomcolor", "Random color", SettingCategory.Intern, null, true));
		this.addSetting(new SettingColor("animation.breath.color", "Color", SettingCategory.Intern,	null, Color.RED));
	}
	
	@Override
	public void onLoop() {
		if(!((SettingBoolean) getSetting("animation.breath.randomcolor")).get()) {
			color = ((SettingColor) getSetting("animation.breath.color")).get();
		}
		Color tmp = color;
		
		if(darker) {
			if(--brghtns <= 5) {
				darker = false;
			}
		} else {
			if(++brghtns >= 100) {
				darker = true;
				if(++nxtColorCounter == 2) {
					color = RainbowWheel.getRandomColor();
					nxtColorCounter = 0;
				}
			}
		}
		tmp = ColorUtil.dimColor(tmp, brghtns);
		OutputManager.addToOutput(PixelColorUtils.colorAllPixels(tmp, RemoteLightCore.getLedNum()));
		super.onLoop();
	}
	
	@Override
	public void onSettingUpdate() {
		this.hideSetting("animation.breath.color", getSetting(SettingBoolean.class, "animation.breath.randomcolor").get());
		super.onSettingUpdate();
	}

}
