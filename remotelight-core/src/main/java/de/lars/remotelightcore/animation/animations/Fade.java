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
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.utils.color.ColorUtil;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class Fade extends Animation {

	private Color color = Color.RED;
	private int dimVal = 100;

	public Fade() {
		super("Fade");
		this.addSetting(new SettingBoolean("animation.fade.randomcolor", "Random color", SettingCategory.Intern, null, true));
		this.addSetting(new SettingColor("animation.fade.color", "Color", SettingCategory.Intern,	null, Color.RED));
	}

	@Override
	public void onLoop() {
		if (dimVal <= 1) {
			color = RainbowWheel.getRandomColor();
			dimVal = 100;
		}
		dimVal--;
		
		if(!((SettingBoolean) getSetting("animation.fade.randomcolor")).getValue()) {
			color = ((SettingColor) getSetting("animation.fade.color")).getValue();
		}

		Color c = ColorUtil.dimColor(color, dimVal);
		OutputManager.addToOutput(PixelColorUtils.colorAllPixels(c, RemoteLightCore.getLedNum()));

		super.onLoop();
	}

}
