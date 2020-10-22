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

import de.lars.remotelightcore.utils.color.Color;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.musicsync.MusicEffect;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.utils.color.ColorUtil;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class Visualizer extends MusicEffect {
	
	private Color[] strip;
	private boolean rainbow = false;
	private SettingBoolean sRainbow;
	private SettingColor sColor;

	public Visualizer() {
		super("Visualizer");
		sRainbow = this.addSetting(new SettingBoolean("musicsync.visualizer.rainbow", "Rainbow", SettingCategory.MusicEffect, "", false));
		sColor = this.addSetting(new SettingColor("musicsync.visualizer.color", "Color", SettingCategory.MusicEffect, "", Color.RED));
	}
	
	@Override
	public void onEnable() {
		strip = new Color[RemoteLightCore.getLedNum()];
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		if(rainbow != sRainbow.get()) {
			rainbow = sRainbow.get();
			this.hideSetting(sColor, rainbow);
			this.updateEffectOptions();
		}
		
		float[] ampl = getSoundProcessor().getAmplitudes(); //amplitudes
		int[] fftData = getSoundProcessor().computeFFT(ampl, strip.length, getAdjustment());
		
		for(int i = 0; i < RemoteLightCore.getLedNum(); i++) {
			int brightness = fftData[i];
			
			Color c = ColorUtil.dimColor(getColor(i), brightness);
			strip[i] = c;
		}
		
		OutputManager.addToOutput(strip);
		super.onLoop();
	}
	
	private Color getColor(int led) {
		if(rainbow) {
			int mltiplr = RainbowWheel.getRainbow().length / RemoteLightCore.getLedNum();
			return RainbowWheel.getRainbow()[led * mltiplr];
		} else {
			return sColor.get();
		}
	}

}
