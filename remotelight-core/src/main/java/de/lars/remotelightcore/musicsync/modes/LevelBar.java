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
import java.util.ArrayList;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.musicsync.MusicEffect;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class LevelBar extends MusicEffect {
	
	private Color[] strip;
	private Color background = Color.BLACK;
	private Color color1;
	private Color color2;
	private Color color3;
	private boolean autoChange;
	private boolean smooth;
	private ArrayList<Color[]> pattern = new ArrayList<>();
	private int count; // pattern counter
	private int lastLeds = 0; // last amount of leds glowing
	private int pix; // number of leds
	private int half; // half the number of leds

	public LevelBar() {
		super("LevelBar");
		this.addSetting(new SettingColor("musicsync.levelbar.color1", "Color 1", SettingCategory.MusicEffect, "", Color.RED));
		this.addSetting(new SettingColor("musicsync.levelbar.color2", "Color 2", SettingCategory.MusicEffect, "", Color.RED));
		this.addSetting(new SettingColor("musicsync.levelbar.color3", "Color 3", SettingCategory.MusicEffect, "", Color.RED));
		this.addSetting(new SettingBoolean("musicsync.levelbar.autochange", "AutoChange", SettingCategory.MusicEffect, "Automatically change color", false));
		this.addSetting(new SettingBoolean("musicsync.levelbar.smooth", "Smooth", SettingCategory.MusicEffect, "", false));
	}
	
	@Override
	public void onEnable() {
		this.initPattern();
		this.initOptions();
		
		pix = RemoteLightCore.getLedNum();
		half = pix / 2;
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, pix);
		
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		this.initOptions();
		
		double mul = 0.1 * this.getAdjustment() * RemoteLightCore.getLedNum() / 60; // multiplier for amount of pixels
		int[] amp = getSoundProcessor().getSimpleAmplitudes(); //6 bands
		int ampAv; //average of all amp bands
		int x = 0;
		for(int i = 0; i < amp.length; i++) {
			x += amp[i];
		}
		ampAv = x / amp.length; //average of all amp bands
		
		int leds = (int) (ampAv * mul); //how many leds should glow
		if(leds > half) leds = half;
		
		//Smooth
		if(smooth) {
			if(lastLeds > leds) {
				leds = lastLeds;
				lastLeds--;
			} else {
				lastLeds = leds;
			}
				
		}
		
		if(isBump()) {
			if(count < pattern.size() - 1)
				count++;
			else
				count = 0;
		}
		
		
		//half 1
		for(int i = 0; i < half; i++) {
			Color c = color1;
			
			if(autoChange) //auto color change
				c = pattern.get(count)[0];
			
			if((half - i) > leds) {
				c = background;
			} else {
				if(i < half / 3) {
					c = color3;
					
					if(autoChange) //auto color change
						c = pattern.get(count)[2];
				}
				else if(i < (half / 3) * 2) {
					c = color2;
					
					if(autoChange) //auto color change
						c = pattern.get(count)[1];
				}
			}
			strip[i] = c;
		}
		
		//half 2
		for(int i = 0; i < half; i++) {
			Color c = background;
			if(i < leds) {
				c = color1;
				
				if(autoChange) //auto color change
					c = pattern.get(count)[0];
				
					if(i >= (half / 3) * 2) {
						c = color3;
						
						if(autoChange) //auto color change
							c = pattern.get(count)[2];
					}
					else if(i >= half / 3) {
						c = color2;
						
						if(autoChange) //auto color change
							c = pattern.get(count)[1];
					}
			}
			strip[i + half] = c;
		}
		
		OutputManager.addToOutput(strip);
		super.onLoop();
	}
	
	private void initPattern() {
		pattern.add(new Color[] {Color.RED, new Color(255, 114, 0), new Color(255, 178, 0)});
		pattern.add(new Color[] {new Color(7, 0, 255), new Color(0, 146, 255), new Color(0, 255, 185)});
		pattern.add(new Color[] {new Color(100, 0, 255), new Color(212, 0, 255), new Color(255, 0, 154)});
		pattern.add(new Color[] {Color.RED, new Color(252, 197, 0), new Color(123, 255, 0)});
		pattern.add(new Color[] {Color.GREEN, new Color(185, 255, 0), new Color(255, 247, 0)});
		pattern.add(new Color[] {Color.GREEN, new Color(0, 255, 29), new Color(0, 255, 146)});
		pattern.add(new Color[] {new Color(255, 92, 0), new Color(0, 255, 46), new Color(254, 255, 0)});
		pattern.add(new Color[] {new Color(0, 51, 51), new Color(0, 0, 51), new Color(54, 61, 0)});
		pattern.add(new Color[] {new Color(51, 0, 0), new Color(0, 0, 51), new Color(0, 15, 0)});
		pattern.add(new Color[] {Color.RED, new Color(255, 10, 0), new Color(71, 18, 0)});
		
	}
	
	private void initOptions() {
		color1 = ((SettingColor) getSetting("musicsync.levelbar.color1")).getValue();
		color2 = ((SettingColor) getSetting("musicsync.levelbar.color2")).getValue();
		color3 = ((SettingColor) getSetting("musicsync.levelbar.color3")).getValue();
		autoChange = ((SettingBoolean) getSetting("musicsync.levelbar.autochange")).getValue();
		smooth = ((SettingBoolean) getSetting("musicsync.levelbar.smooth")).getValue();
	}

}
