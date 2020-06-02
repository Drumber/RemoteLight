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
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.utils.color.ColorUtil;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;
import de.lars.remotelightcore.utils.maths.MathHelper;
import de.lars.remotelightcore.utils.maths.SimplexNoise;

public class Muddle extends Animation {
	
	private final int rainbowLength = RainbowWheel.getRainbow().length;
	private int amount;
	private Color[] strip;
	private Color[] colors;
	private double[] x, y;

	public Muddle() {
		super("Muddle");
		this.addSetting(new SettingBoolean("animation.muddle.rainbow", "Rainbow", SettingCategory.Intern, null, false));
	}
	
	@Override
	public void onEnable() {
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum());
		amount = strip.length / 15;
		y = new double[amount];
		x = new double[amount];
		colors = new Color[amount];
		
		for(int i = 0; i < amount; i++) {
			y[i] = new Random().nextInt(999999);
			x[i] = new Random().nextDouble();
			colors[i] = RainbowWheel.getRainbow()[rainbowLength / amount * i];
		}
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		for(int i = 0; i < amount; i++) {
		
			double noise = SimplexNoise.noise(x[i], y[i]);
			x[i] += 0.008;
			
			int pos = (int) MathHelper.lerp(0, strip.length / 2, (float) Math.abs(noise));
			int hue = (int) MathHelper.lerp(0, rainbowLength / 2, (float) Math.abs(noise));
			
			if(noise < 0) {
				pos = strip.length / 2 - pos;
				hue = rainbowLength / 2 - hue;
			} else {
				pos += strip.length / 2;
				hue += rainbowLength / 2;
			}
			
			Color color = colors[i];
			if(((SettingBoolean) getSetting("animation.muddle.rainbow")).getValue()) {	// rainbow mode
				color = RainbowWheel.getRainbow()[hue];
			}
			strip[pos] = color;
		}
		
		OutputManager.addToOutput(strip);
		strip = ColorUtil.dimColorSimple(strip, 20);
		super.onLoop();
	}

}
