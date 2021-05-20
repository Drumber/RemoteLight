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
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightcore.utils.color.Color;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class TwoColors extends Animation {
	
	private Color[] strip;
	private final String[] DIRECTIONS = {"Left", "Center", "Right"};
	private int colorCounter = 0;
	private boolean color1 = true;

	public TwoColors() {
		super("Two Colors");
		this.addSetting(new SettingSelection("animation.twocolors.direction", "Direction", SettingCategory.Intern, null, DIRECTIONS, DIRECTIONS[0], SettingSelection.Model.ComboBox));
		this.addSetting(new SettingInt("animation.twocolors.groupsize", "Group size", SettingCategory.Intern, null, 2, 1, 50, 1));
		this.addSetting(new SettingColor("animation.twocolors.color1", "Color 1", SettingCategory.Intern,	null, Color.RED));
		this.addSetting(new SettingColor("animation.twocolors.color2", "Color 2", SettingCategory.Intern,	null, Color.GREEN));
	}
	
	@Override
	public void onEnable(int pixels) {
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, pixels);
		// this fills the strip when activating the animation
		for(int i = 0; i < pixels; i++) {
			onEffect();
		}
		super.onEnable(pixels);
	}
	
	@Override
	public Color[] onEffect() {
		final int groupSize = getSetting(SettingInt.class, "animation.twocolors.groupsize").get();
		final String direction = getSetting(SettingSelection.class, "animation.twocolors.direction").get();
		int newPixelPos = 0;
		
		// shift the pixels depending on the direction
		if(direction.equalsIgnoreCase(DIRECTIONS[0])) {
			strip = PixelColorUtils.shiftPixelsLeft(strip, 1);
			newPixelPos = strip.length -  1;
		} else if(direction.equalsIgnoreCase(DIRECTIONS[1])) {
			strip = PixelColorUtils.shiftPixelsCenter(strip, 1);
			newPixelPos = strip.length / 2 - 1;
		} else {
			strip = PixelColorUtils.shiftPixelsRight(strip, 1);
			newPixelPos = 0;
		}
		
		// switch color when the counter reaches the group size
		if(++colorCounter >= groupSize) {
			colorCounter = 0;
			color1 = !color1;
		}
		
		Color c;
		if(color1) {
			c = getSetting(SettingColor.class, "animation.twocolors.color1").get();
		} else {
			c = getSetting(SettingColor.class, "animation.twocolors.color2").get();
		}
		
		strip[newPixelPos] = c;
		if(direction.equalsIgnoreCase(DIRECTIONS[1])) 	// set both pixels in the middle of the strip
			strip[newPixelPos+1] = c;	// if the direction is center
		return strip;
	}

}
