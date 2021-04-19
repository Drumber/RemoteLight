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

import java.util.Random;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.musicsync.MusicEffect;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightcore.settings.types.SettingSelection.Model;
import de.lars.remotelightcore.utils.color.Color;
import de.lars.remotelightcore.utils.color.ColorUtil;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class Bars extends MusicEffect {
	
	private String mode;
	private Color[] strip;
	private Color color;
	private int hue;
	private boolean lastPeak;
	private double avgVol;
	private int barWidth = 5;

	public Bars() {
		super("Bars");
		this.addSetting(new SettingInt("musicsync.bars.barwidth", "Bar width", SettingCategory.MusicEffect, null, 5, 1, 20, 1));
		String[] modes = new String[] {"Rainbow", "Frequency", "Random", "Static"};
		this.addSetting(new SettingSelection("musicsync.bars.mode", "Color mode", SettingCategory.MusicEffect, null, modes, "Static", Model.ComboBox));
		this.addSetting(new SettingColor("musicsync.bars.color", "Color", SettingCategory.MusicEffect, null, Color.RED));
	}
	
	@Override
	public void onEnable() {
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum());
		lastPeak = false;
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		barWidth = ((SettingInt) getSetting("musicsync.bars.barwidth")).get();	// get bar width from settings
		
		double vol = this.getSpl();
		avgVol = (vol + getMaxSpl() * (0.8 + getSensitivity() / 20)) / 2;	// smooth max volume and calculate average
		
		boolean peak = (vol > avgVol) && !lastPeak;	// peak detection
		lastPeak = peak;
		
		if(peak) {
			if(barWidth >= strip.length) barWidth = strip.length - 1; // prevent errors
			
			setColor();	// set color from chosen mode
			int pos = new Random().nextInt(strip.length - barWidth); // random position
			for(int i = 0; i < barWidth; i++) {
				strip[pos + i] = color;
			}
		}
		
		if(++hue > RainbowWheel.getRainbow().length) {	// increment hue value for rainbow mode
			hue = 0;
		}
		
		OutputManager.addToOutput(strip);
		strip = ColorUtil.dimColorSimple(strip, 10);
		super.onLoop();
	}
	
	
	private void setColor() {
		String prevMode = mode;
		mode = getSetting(SettingSelection.class, "musicsync.bars.mode").getSelected();
		if(!mode.equals(prevMode)) {
			// hide color option when mode is not static
			this.hideSetting("musicsync.bars.color", !mode.equals("Static"));
			this.updateEffectOptions();
		}
		
		switch (mode.toLowerCase()) {
			case "static":
				color = getSetting(SettingColor.class, "musicsync.bars.color").get();
				break;
			case "frequency":
				color = ColorUtil.soundToColor((int) this.getPitch());
				break;
			case "random":
				color = RainbowWheel.getRandomColor();
				break;
			case "rainbow": {
				int ranHue = new Random().nextInt(15) + hue;
				if(ranHue >= RainbowWheel.getRainbow().length) {
					ranHue -= RainbowWheel.getRainbow().length;
				}
				color = RainbowWheel.getRainbow()[ranHue];
				break;
			}
		}
	}

}
