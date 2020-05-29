/*******************************************************************************
 * ______                     _       _     _       _     _   
 * | ___ \                   | |     | |   (_)     | |   | |  
 * | |_/ /___ _ __ ___   ___ | |_ ___| |    _  __ _| |__ | |_ 
 * |    // _ \ '_ ` _ \ / _ \| __/ _ \ |   | |/ _` | '_ \| __|
 * | |\ \  __/ | | | | | (_) | ||  __/ |___| | (_| | | | | |_ 
 * \_| \_\___|_| |_| |_|\___/ \__\___\_____/_|\__, |_| |_|\__|
 *                                             __/ |          
 *                                            |___/           
 * 
 * Copyright (C) 2019 Lars O.
 * 
 * This file is part of RemoteLight.
 ******************************************************************************/
package de.lars.remotelightcore.musicsync.modes;

import java.awt.Color;
import java.util.ArrayList;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.musicsync.MusicEffect;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class LevelBar extends MusicEffect {
	
	private SettingsManager s = RemoteLightCore.getInstance().getSettingsManager();
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
		
		s.addSetting(new SettingColor("musicsync.levelbar.color1", "Color 1", SettingCategory.MusicEffect, "", Color.RED));
		this.addOption("musicsync.levelbar.color1");
		s.addSetting(new SettingColor("musicsync.levelbar.color2", "Color 2", SettingCategory.MusicEffect, "", Color.RED));
		this.addOption("musicsync.levelbar.color2");
		s.addSetting(new SettingColor("musicsync.levelbar.color3", "Color 3", SettingCategory.MusicEffect, "", Color.RED));
		this.addOption("musicsync.levelbar.color3");
		s.addSetting(new SettingBoolean("musicsync.levelbar.autochange", "AutoChange", SettingCategory.MusicEffect, "Automatically change color", false));
		this.addOption("musicsync.levelbar.autochange");
		s.addSetting(new SettingBoolean("musicsync.levelbar.smooth", "Smooth", SettingCategory.MusicEffect, "", false));
		this.addOption("musicsync.levelbar.smooth");
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
		color1 = ((SettingColor) s.getSettingFromId("musicsync.levelbar.color1")).getValue();
		color2 = ((SettingColor) s.getSettingFromId("musicsync.levelbar.color2")).getValue();
		color3 = ((SettingColor) s.getSettingFromId("musicsync.levelbar.color3")).getValue();
		autoChange = ((SettingBoolean) s.getSettingFromId("musicsync.levelbar.autochange")).getValue();
		smooth = ((SettingBoolean) s.getSettingFromId("musicsync.levelbar.smooth")).getValue();
	}

}
