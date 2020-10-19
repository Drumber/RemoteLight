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

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.musicsync.MusicEffect;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class Rainbow extends MusicEffect {
	
	private Color[] strip;
	private int pix;
	private int half;
	private int lastLeds = 0;
	private Color[] rainbow;
	private int step = 0;
	
	public boolean smoothRise = true;
	public boolean smoothFall = true;
	public int steps = 5;

	public Rainbow() {
		super("Rainbow");
		this.addSetting(new SettingBoolean("musicsync.rainbow.smoothrise", "Smooth Rise", SettingCategory.MusicEffect, "", true));
		this.addSetting(new SettingBoolean("musicsync.rainbow.smoothfall", "Smooth Fall", SettingCategory.MusicEffect, "", true));
		this.addSetting(new SettingInt("musicsync.rainbow.steps", "Steps", SettingCategory.MusicEffect, "", 5, 1, 20, 1));
	}
	
	private void initOptions() {
		smoothRise = ((SettingBoolean) getSetting("musicsync.rainbow.smoothrise")).get();
		smoothFall= ((SettingBoolean) getSetting("musicsync.rainbow.smoothfall")).get();
		steps = ((SettingInt) getSetting("musicsync.rainbow.steps")).get();
	}
	
	@Override
	public void onEnable() {
		this.initOptions();
		
		pix = getLeds();
		half = pix / 2;
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, pix);
		rainbow = new Color[pix];
		
		for(int i = 0; i < half; i++) {
			step += 5;
			if(step >= RainbowWheel.getRainbow().length)
				step = 0;
			
			Color c = RainbowWheel.getRainbow()[step];
			//half1
			rainbow[(half - 1 - i)] = c;
			//half2
			rainbow[(half + i)] = c;
		}
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		this.initOptions();
		
		double mul = 0.2 * this.getAdjustment() * RemoteLightCore.getLedNum() / 60; // multiplier for amount of pixels
		int[] amp = getSoundProcessor().getSimpleAmplitudes(); //6 bands
		
		int x = 0;
		for(int i = 0; i < amp.length; i++) {
			x += amp[i];
		}
		int ampAv = x / amp.length; //average of all amp bands
		
		int leds = (int) (ampAv * mul); //how many leds should glow
		if(leds > half) leds = half;
		
		//Smooth
		if(smoothRise && smoothFall) {
			if(lastLeds > leds) {
				leds = lastLeds;
				lastLeds--;
			} else {
				lastLeds += 2;
				if(lastLeds > leds) lastLeds = leds;
				leds = lastLeds;
			}
			
		} else if(smoothRise) {
			if(lastLeds > leds) {
				lastLeds = leds;
			} else {
				lastLeds += 2;
				if(lastLeds > leds) lastLeds = leds;
				leds = lastLeds;
			}
			
		} else if(smoothFall) {
			if(lastLeds > leds) {
				leds = lastLeds;
				lastLeds--;
			} else {
				lastLeds = leds;
			}
		}
		
		
		//Rainbow
		for(int i = 0; i < pix; i++) {
			strip[i] = rainbow[i];
		}
		
		if(isBump())
			step += steps + (steps / 2) + 20;
		else
			step += steps;

		if(step >= RainbowWheel.getRainbow().length)
			step = 0;
		
		//move
		for(int i = 0; i < half; i++) {
			Color c = RainbowWheel.getRainbow()[step];
			
			if(i == (half - 1)) {
				rainbow[i] = c;	//half1
				rainbow[half] = c;//half2
				
			} else {
				rainbow[i] = rainbow[i + 1];
				rainbow[pix - 1 - i] = rainbow[pix - 2 - i];
			}
		}
		
		//effect
		for(int i = 0; i < (half - leds); i++) {
			strip[i] = Color.BLACK;
			strip[pix - 1 - i] = Color.BLACK;
		}
		
		OutputManager.addToOutput(strip);
		super.onLoop();
	}

}
