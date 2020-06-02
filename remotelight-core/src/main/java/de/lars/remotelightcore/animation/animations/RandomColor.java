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

import java.util.Random;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class RandomColor extends Animation {
	
	private int pos;
	private String lastMode;

	public RandomColor() {
		super("RandomColor");
		this.addSetting(new SettingSelection("animation.randomcolor.mode", "Mode", SettingCategory.Intern, "",
				new String[] {"Mode 1", "Mode 2"}, "Mode 1", SettingSelection.Model.ComboBox));
		lastMode = ((SettingSelection) this.getSetting("animation.randomcolor.mode")).getSelected();
	}
	
	@Override
	public void onLoop() {
		String mode = ((SettingSelection) this.getSetting("animation.randomcolor.mode")).getSelected();
		if(!mode.equals(lastMode)) {
			PixelColorUtils.setAllPixelsBlack();
			lastMode = mode;
			pos = 0;
		}
		
		if(mode.equals("Mode 1")) {
			mode1();
		} else if(mode.equals("Mode 2")) {
			mode2();
		}
		super.onLoop();
	}
	
	private void mode1() {
		PixelColorUtils.setPixel(pos, RainbowWheel.getRandomColor());
		if(++pos >= RemoteLightCore.getLedNum()) {
			pos = 0;
		}
	}
	
	private void mode2() {
		int rnd = new Random().nextInt(RemoteLightCore.getLedNum());
		
		PixelColorUtils.setPixel(rnd, RainbowWheel.getRandomColor());
	}

}
