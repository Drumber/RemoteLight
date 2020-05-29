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

import de.lars.remotelightcore.Main;
import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class Open extends Animation {
	
	private Color[] strip;
	private int pos;
	private Color color;
	private boolean fadeOut;

	public Open() {
		super("Open");
		color = RainbowWheel.getRandomColor();
		this.addSetting(new SettingBoolean("animation.open.randomcolor", "Random color", SettingCategory.Intern, null, true));
		this.addSetting(new SettingColor("animation.open.color", "Color", SettingCategory.Intern,	null, Color.RED));
		this.addSetting(new SettingBoolean("animation.open.fadeout", "Fade out", SettingCategory.Intern, null, true));
	}
	
	@Override
	public void onEnable() {
		fadeOut = false;
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, Main.getLedNum());
		pos = strip.length / 2;
		color = RainbowWheel.getRandomColor();
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		int half = strip.length / 2;
		
		if(!((SettingBoolean) getSetting("animation.open.randomcolor")).getValue()) {
			color = ((SettingColor) getSetting("animation.open.color")).getValue();
		}
		
		strip[pos] = color;
		for(int i = 0; i < half; i++) {
			if(i <= pos) {
				if(fadeOut)
					strip[i] = Color.BLACK;
				else
					strip[i] = color;
			} else {
				if(fadeOut)
					strip[i] = color;
				else
					strip[i] = Color.BLACK;
			}
		}
		
		if(--pos == 0) {
			pos = half;
			if(((SettingBoolean) getSetting("animation.open.randomcolor")).getValue() && !fadeOut) {
				color = RainbowWheel.getRandomColor();
			}
			if(((SettingBoolean) getSetting("animation.open.fadeout")).getValue()) {
				fadeOut = !fadeOut;
			} else {
				fadeOut = false;
			}
		}
		
		strip = PixelColorUtils.centered(strip, false);
		
		OutputManager.addToOutput(strip);
		super.onLoop();
	}
	
}
