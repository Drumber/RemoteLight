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

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.musicsync.MusicEffect;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class Rainbow extends MusicEffect {
	
	private SettingsManager s = RemoteLightCore.getInstance().getSettingsManager();
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
		
		s.addSetting(new SettingBoolean("musicsync.rainbow.smoothrise", "SmoothRise", SettingCategory.MusicEffect, "", true));
		this.addOption("musicsync.rainbow.smoothrise");
		s.addSetting(new SettingBoolean("musicsync.rainbow.smoothfall", "SmoothFall", SettingCategory.MusicEffect, "", true));
		this.addOption("musicsync.rainbow.smoothfall");
		s.addSetting(new SettingInt("musicsync.rainbow.steps", "Steps", SettingCategory.MusicEffect, "", 5, 1, 20, 1));
		this.addOption("musicsync.rainbow.steps");
	}
	
	private void initOptions() {
		smoothRise = ((SettingBoolean) s.getSettingFromId("musicsync.rainbow.smoothrise")).getValue();
		smoothFall= ((SettingBoolean) s.getSettingFromId("musicsync.rainbow.smoothfall")).getValue();
		steps = ((SettingInt) s.getSettingFromId("musicsync.rainbow.steps")).getValue();
	}
	
	@Override
	public void onEnable() {
		this.initOptions();
		
		pix = RemoteLightCore.getLedNum();
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
		
		double mul = 0.1 * this.getAdjustment() * RemoteLightCore.getLedNum() / 60; // multiplier for amount of pixels
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
