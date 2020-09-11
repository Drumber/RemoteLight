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
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class RunningLight extends Animation {
	
	private Color[] color;
	private int pass, counter;

	public RunningLight() {
		super("RunningLight");
		this.addSetting(new SettingBoolean("animation.runninglight.randomcolor", "Random color", SettingCategory.Intern, null, true));
		this.addSetting(new SettingColor("animation.runninglight.color", "Color", SettingCategory.Intern,	null, Color.RED));
	}
	
	@Override
	public void onEnable() {
	    this.color = new Color[] {Color.RED, Color.ORANGE, Color.PINK, Color.MAGENTA,
	    		Color.BLUE, Color.CYAN, Color.GREEN};
	    pass = 0; counter = 0;
	    
	    for(int i = 0; i < RemoteLightCore.getLedNum(); i++) {
	    	onLoop();
	    }
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		if(counter >= color.length) {
			counter = 0;
		}
		Color c = color[counter];
		
		if(!((SettingBoolean) getSetting("animation.runninglight.randomcolor")).getValue()) {
			c = ((SettingColor) getSetting("animation.runninglight.color")).getValue();
		}
		
		if(pass < 5) {
			switch (pass) {
			case 0:
				PixelColorUtils.setPixel(0, c.darker().darker());
				break;
			case 1:
				PixelColorUtils.setPixel(0, c.darker());
				break;
			case 2:
				PixelColorUtils.setPixel(0, c);
				break;
			case 3:
				PixelColorUtils.setPixel(0, c.darker());
				break;
			case 4:
				PixelColorUtils.setPixel(0, c.darker().darker());
				counter++;
				break;
			}
			
			pass++;
		} else if(pass < 10) {
			pass++;
			PixelColorUtils.setPixel(0, Color.BLACK);
		} else {
			pass = 0;
			PixelColorUtils.setPixel(0, Color.BLACK);
		}
		
		//shift pixels to right
		PixelColorUtils.shiftRight(1);
		
		super.onLoop();
	}
	
	@Override
	public void onSettingUpdate() {
		this.hideSetting("animation.runninglight.color", getSetting(SettingBoolean.class, "animation.runninglight.randomcolor").getValue());
		super.onSettingUpdate();
	}

}
