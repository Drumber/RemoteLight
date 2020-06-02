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

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class Rainbow extends Animation {
	
	private int step;

	public Rainbow() {
		super("Rainbow");
		this.addSetting(new SettingBoolean("animation.rainbow.cycle", "Cycle", SettingCategory.Intern, null, true));
	}
	
	@Override
	public void onEnable() {
		step = 0;
		if(((SettingBoolean) getSetting("animation.rainbow.cycle")).getValue()) {
			for(int i = 0; i < RemoteLightCore.getLedNum(); i++) {
				PixelColorUtils.shiftRight(1);
				step += 3;
				if(step >= RainbowWheel.getRainbow().length) {
					step = 0;
				}
				PixelColorUtils.setPixel(0, RainbowWheel.getRainbow()[step]);
			}
		}
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		if(step >= RainbowWheel.getRainbow().length) {
			step = 0;
		}
		
		if(((SettingBoolean) getSetting("animation.rainbow.cycle")).getValue()) {
			PixelColorUtils.shiftRight(1);
			PixelColorUtils.setPixel(0, RainbowWheel.getRainbow()[step]);
			step += 3;
		} else {
			OutputManager.addToOutput(PixelColorUtils.colorAllPixels(RainbowWheel.getRainbow()[step], RemoteLightCore.getLedNum()));
			step++;
		}
		super.onLoop();
	}

}
