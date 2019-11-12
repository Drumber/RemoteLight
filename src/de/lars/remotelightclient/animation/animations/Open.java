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
package de.lars.remotelightclient.animation.animations;

import java.awt.Color;
import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.animation.Animation;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;
import de.lars.remotelightclient.settings.types.SettingBoolean;
import de.lars.remotelightclient.settings.types.SettingColor;
import de.lars.remotelightclient.utils.PixelColorUtils;
import de.lars.remotelightclient.utils.RainbowWheel;

public class Open extends Animation {
	
	private int pos;
	private Color color;

	public Open() {
		super("Open");
		color = RainbowWheel.getRandomColor();
		this.addSetting(new SettingBoolean("animation.open.randomcolor", "Random color", SettingCategory.Intern, null, true));
		this.addSetting(new SettingColor("animation.open.color", "Color", SettingCategory.Intern,	null, Color.RED));
	}
	
	@Override
	public void onLoop() {
		int half = Main.getLedNum() / 2;
		Color[] strip = Main.getInstance().getOutputManager().getLastColors();
		
		if(!((SettingBoolean) getSetting("animation.open.randomcolor")).getValue()) {
			color = ((SettingColor) getSetting("animation.open.color")).getValue();
		}
		
		strip[pos] = color;
		for(int i = 0; i < half; i++) {
			if(i < pos) {
				strip[i] = color;
			} else {
				strip[i] = Color.BLACK;
			}
		}
		
		if(pos-- == 0) {
			pos = half;
			if(((SettingBoolean) getSetting("animation.open.randomcolor")).getValue()) {
				color = RainbowWheel.getRandomColor();
			}
			strip = PixelColorUtils.colorAllPixels(Color.BLACK, strip.length);
			strip[0] = color;
		}
		
		strip = PixelColorUtils.centered(strip, false);
		
		OutputManager.addToOutput(strip);
		super.onLoop();
	}
	
}
