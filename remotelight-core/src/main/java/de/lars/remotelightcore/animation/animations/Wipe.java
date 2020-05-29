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
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.utils.color.ColorUtil;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class Wipe extends Animation {
	
	private int pos;
	private Color color;

	public Wipe() {
		super("Wipe");
		this.addSetting(new SettingBoolean("animation.wipe.randomcolor", "Random color", SettingCategory.Intern, null, true));
		this.addSetting(new SettingColor("animation.wipe.color", "Color", SettingCategory.Intern,	null, Color.RED));
	}
	
	@Override
	public void onEnable() {
		pos = 0;
		color = RainbowWheel.getRandomColor();
		if(!((SettingBoolean) getSetting("animation.wipe.randomcolor")).getValue()) {
			color = ((SettingColor) getSetting("animation.wipe.color")).getValue();
		}
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		PixelColorUtils.setPixel(pos, color);
		
		if(++pos == RemoteLightCore.getLedNum()) {
			pos = 0;
			
			if(!((SettingBoolean) getSetting("animation.wipe.randomcolor")).getValue()) {
				if(color == Color.BLACK) {
					color = ((SettingColor) getSetting("animation.wipe.color")).getValue();
				} else {
					color = Color.BLACK;
				}
			} else {
				color = getRandomColor();
			}
		}
		super.onLoop();
	}
	
	private Color getRandomColor() {
		Color c = Color.RED;
		do {
			c = RainbowWheel.getRandomColor();
		} while(ColorUtil.similar(c, color));
		return c;
	}

}
