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
package de.lars.remotelightcore.animation.animations;

import java.awt.Color;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class Close extends Animation {
	
	private Color[] strip;
	private int pos;
	private Color color;
	private boolean fadeToBlack;

	public Close() {
		super("Close");
		color = RainbowWheel.getRandomColor();
		this.addSetting(new SettingBoolean("animation.close.randomcolor", "Random color", SettingCategory.Intern, null, true));
		this.addSetting(new SettingColor("animation.close.color", "Color", SettingCategory.Intern, null, Color.RED));
		this.addSetting(new SettingBoolean("animation.close.fadeout", "Fade out", SettingCategory.Intern, null, true));
	}
	
	@Override
	public void onEnable() {
		fadeToBlack = false;
		pos = 0;
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum());
		color = RainbowWheel.getRandomColor();
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		int half = strip.length / 2;
		
		if(!((SettingBoolean) getSetting("animation.close.randomcolor")).getValue() && !fadeToBlack) {
			color = ((SettingColor) getSetting("animation.close.color")).getValue();
		}
		
		strip[pos] = color;
		for(int i = 0; i < half; i++) {
			if(i <= pos) {
				strip[i] = color;
			}
		}
		
		if(pos++ == half) {
			pos = 0;
			fadeToBlack = !fadeToBlack;
			color = RainbowWheel.getRandomColor();
			if(!((SettingBoolean) getSetting("animation.close.fadeout")).getValue()) {
				strip = PixelColorUtils.colorAllPixels(Color.BLACK, strip.length);
			} else if(fadeToBlack) {
				color = Color.BLACK;
			}
		}
		
		strip = PixelColorUtils.centered(strip, false);
		
		OutputManager.addToOutput(strip);
		super.onLoop();
	}

}
