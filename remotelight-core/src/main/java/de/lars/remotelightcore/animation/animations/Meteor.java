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
import java.util.Random;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class Meteor extends Animation {
	
	private int pos = 0;
	private boolean right = true;
	private Color color;

	public Meteor() {
		super("Meteor");
		color = RainbowWheel.getRandomColor();
		this.addSetting(new SettingBoolean("animation.meteor.randomcolor", "Random color", SettingCategory.Intern, null, true));
		this.addSetting(new SettingColor("animation.meteor.color", "Color", SettingCategory.Intern,	null, Color.RED));
		this.addSetting(new SettingBoolean("animation.meteor.sparkletrail", "Sparkle trail", SettingCategory.Intern, null, true));
	}
	
	@Override
	public void onLoop() {
		Color[] strip = RemoteLightCore.getInstance().getOutputManager().getLastColors();
		
		if(!((SettingBoolean) getSetting("animation.meteor.randomcolor")).getValue()) {
			color = ((SettingColor) getSetting("animation.meteor.color")).getValue();
		}
		
		for(int i = 0; i < RemoteLightCore.getLedNum(); i++) {
			boolean sparkle = ((SettingBoolean) getSetting("animation.meteor.sparkletrail")).getValue();
			if(!sparkle || new Random().nextInt(10) > 5) {
				strip[i] = dim(strip[i]);
			}
		}
		strip[pos] = color;
		
		if(right) {
			if(++pos == RemoteLightCore.getLedNum()) {
				pos = RemoteLightCore.getLedNum()-1;
				right = false;
			}
		}
		else {
			if(--pos < 0) {
				pos = 0;
				right = true;
				color = RainbowWheel.getRandomColor();
			}
		}
		OutputManager.addToOutput(strip);
		
		super.onLoop();
	}
	
	private Color dim(Color c) {
		int r = c.getRed();
		r /= 1.5;
		int g = c.getGreen();
		g /= 1.5;
		int b = c.getBlue();
		b /= 1.5;
		
		if(r < 2) r = 0;
		if(g < 2) g = 0;
		if(b < 2) b = 0;
		
		return new Color(r, g, b);
	}
	
	@Override
	public void onSettingUpdate() {
		this.hideSetting("animation.meteor.color", getSetting(SettingBoolean.class, "animation.meteor.randomcolor").getValue());
		super.onSettingUpdate();
	}

}
