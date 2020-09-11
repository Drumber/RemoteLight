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

package de.lars.remotelightcore.musicsync.modes;

import java.awt.Color;
import java.util.Random;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.musicsync.MusicEffect;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.utils.color.ColorUtil;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;
import de.lars.remotelightcore.utils.maths.MathHelper;

public class Flame extends MusicEffect {
	/* inspired by Andrew Tuline:
	 * https://github.com/atuline/FastLED-SoundReactive/blob/master/notasound/besin.h
	 */
	
	private Color[] strip;
	private int hue;
	private boolean rainbow;

	public Flame() {
		super("Flame");
		this.addSetting(new SettingBoolean("musicsync.flame.rainbow", "Rainbow", SettingCategory.MusicEffect, null, false));
		this.addSetting(new SettingColor("musicsync.flame.color", "Color", SettingCategory.MusicEffect, null, Color.RED));
	}
	
	@Override
	public void onEnable() {
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum());
		hue = 0;
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		if(rainbow != getSetting(SettingBoolean.class, "musicsync.flame.rainbow").getValue()) {
			rainbow = getSetting(SettingBoolean.class, "musicsync.flame.rainbow").getValue();
			// hide color option on rainbow mode
			this.hideSetting("musicsync.flame.color", rainbow);
			this.updateEffectOptions();
		}
		Color color = ((SettingColor) getSetting("musicsync.flame.color")).getValue();
		int ledNum = RemoteLightCore.getLedNum();
		
		if(rainbow) {
			if(isBump()) {
				hue += 8;
			} else {
				hue += 1 + new Random().nextInt(4);
			}
			if(hue >= RainbowWheel.getRainbow().length) hue = 0;
			color = RainbowWheel.getRainbow()[hue];
		}
		
		float vol = (float) (getSpl() / getMaxSpl());	// volume (0..1)
		vol = MathHelper.easeInCubic(vol); // easing value a little
		
		int brightness = (int) MathHelper.lerp(0.0f, 255.0f, vol);
		brightness *= (getAdjustment() / 4);
		color = ColorUtil.dimColorSimple(color, 255 - brightness);
		
		strip[ledNum/2] = color;
		strip[ledNum/2 - 1] = color;
		
		OutputManager.addToOutput(strip);
		PixelColorUtils.shiftCenter(1);
		strip = ColorUtil.dimColorSimple(strip, 5); // fade to black
		super.onLoop();
	}

}
