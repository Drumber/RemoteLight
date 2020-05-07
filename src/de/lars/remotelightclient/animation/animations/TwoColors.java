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
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;
import de.lars.remotelightclient.settings.types.SettingColor;
import de.lars.remotelightclient.utils.color.PixelColorUtils;

public class TwoColors extends Animation {
	
	private int colorCounter = 0;
	private boolean color1 = true;

	public TwoColors() {
		super("Two Colors");
		this.addSetting(new SettingColor("animation.twocolors.color1", "Color 1", SettingCategory.Intern,	null, Color.RED));
		this.addSetting(new SettingColor("animation.twocolors.color2", "Color 2", SettingCategory.Intern,	null, Color.GREEN));
	}
	
	@Override
	public void onEnable() {
		for(int i = 0; i < Main.getLedNum(); i++) {
			onLoop();
		}
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		PixelColorUtils.shiftRight(1);
		
		if(++colorCounter == 2) {
			colorCounter = 0;
			color1 = !color1;
		}
		
		Color c;
		if(color1) {
			c =((SettingColor) getSetting("animation.twocolors.color1")).getValue();
		} else {
			c = ((SettingColor) getSetting("animation.twocolors.color2")).getValue();
		}
		
		PixelColorUtils.setPixel(0, c);
		super.onLoop();
	}

}
