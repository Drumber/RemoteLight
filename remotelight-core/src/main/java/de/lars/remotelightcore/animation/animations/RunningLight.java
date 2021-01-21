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
import de.lars.remotelightcore.utils.color.Color;
import de.lars.remotelightcore.utils.color.ColorUtil;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class RunningLight extends Animation {
	
	private Color[] color;
	private Color[] strip;
	private int pass, counter;

	public RunningLight() {
		super("RunningLight");
		this.addSetting(new SettingBoolean("animation.runninglight.randomcolor", "Random color", SettingCategory.Intern, null, true));
		this.addSetting(new SettingColor("animation.runninglight.color", "Color", SettingCategory.Intern,	null, Color.RED));
	}
	
	@Override
	public void onEnable(int pixels) {
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, pixels);
	    this.color = new Color[] {Color.RED, Color.ORANGE, Color.PINK, Color.MAGENTA,
	    		Color.BLUE, Color.CYAN, Color.GREEN};
	    pass = 0; counter = 0;
	    
	    for(int i = 0; i < pixels; i++) {
	    	onEffect();
	    }
		super.onEnable(pixels);
	}
	
	@Override
	public Color[] onEffect() {
		if(counter >= color.length) {
			counter = 0;
		}
		Color c = color[counter];
		
		if(!((SettingBoolean) getSetting("animation.runninglight.randomcolor")).get()) {
			c = ((SettingColor) getSetting("animation.runninglight.color")).get();
		}
		
		if(pass < 5) {
			switch (pass) {
			case 0:
				strip[0] = ColorUtil.dimColor(c, 70);
				break;
			case 1:
				strip[0] = ColorUtil.dimColor(c, 85);
				break;
			case 2:
				strip[0] = c;
				break;
			case 3:
				strip[0] = ColorUtil.dimColor(c, 85);
				break;
			case 4:
				strip[0] = ColorUtil.dimColor(c, 70);
				counter++;
				break;
			}
			
			pass++;
		} else if(pass < 10) {
			pass++;
			strip[0] = Color.BLACK;
		} else {
			pass = 0;
			strip[0] = Color.BLACK;
		}
		
		//shift pixels to right
		strip = PixelColorUtils.shiftPixelsRight(strip, 1);
		return strip;
	}
	
	@Override
	public void onSettingUpdate() {
		this.hideSetting("animation.runninglight.color", getSetting(SettingBoolean.class, "animation.runninglight.randomcolor").get());
		super.onSettingUpdate();
	}

}
