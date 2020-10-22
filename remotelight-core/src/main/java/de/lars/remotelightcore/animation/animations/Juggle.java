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
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;
import de.lars.remotelightcore.utils.maths.MathHelper;

public class Juggle extends Animation {
	
	private Color[] strip;

	public Juggle() {
		super("Juggle");
		this.addSetting(new SettingInt("animation.juggle.dotsnumber", "Number of dots", SettingCategory.Intern, null, 8, 1, 30, 1));
	}
	
	@Override
	public void onEnable() {
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum());
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		int dotsNum = ((SettingInt) getSetting("animation.juggle.dotsnumber")).get();
		
		dim();
		int dotHue = 0;
		
		for(int i = 0; i < dotsNum; i++) {
			int pos = MathHelper.beatsin16(10+i, strip.length, 0, 0.01);
			strip[pos] = RainbowWheel.getRainbow()[dotHue];
			
			dotHue += 32;
			if(dotHue >= RainbowWheel.getRainbow().length)
				dotHue = 0;
		}
		
		OutputManager.addToOutput(strip);
		super.onLoop();
	}
	
	private void dim() {
		for(int i = 0; i < strip.length; i++) {
			Color c = strip[i];
			int r = c.getRed() - 20;
			int g = c.getGreen() - 20;
			int b = c.getBlue() - 20;
			if(r < 0) r = 0;
			if(g < 0) g = 0;
			if(b < 0) b = 0;
			strip[i] = new Color(r, g, b);
		}
	}

}
