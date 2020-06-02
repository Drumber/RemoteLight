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
import java.util.Random;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.animation.AnimationManager;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.maths.TimeUtil;

public class Twinkle extends Animation {
	
	private AnimationManager am;
	private int max;
	private Color color;
	private TimeUtil time;

	public Twinkle() {
		super("Twinkle");
		this.addSetting(new SettingColor("animation.twinkle.color", "Color", SettingCategory.Intern,	null, new Color(255, 240, 255)));
	}
	
	@Override
	public void onEnable() {
		am = RemoteLightCore.getInstance().getAnimationManager();
		max = RemoteLightCore.getLedNum() / 10;
		time = new TimeUtil(am.getDelay());
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		if(time.hasReached()) {
			OutputManager.addToOutput(PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum()));
			color = ((SettingColor) getSetting("animation.twinkle.color")).getValue();
			
			for(int i = 0; i <= max; i++) {
				if(new Random().nextInt(3) == 0) {
					PixelColorUtils.setPixel(new Random().nextInt(RemoteLightCore.getLedNum()), color);
				}
			}
			
			int rnd = (am.getDelay() * 2) + new Random().nextInt(50);
			time.setInterval(rnd);
		}
		
		super.onLoop();
	}

}
