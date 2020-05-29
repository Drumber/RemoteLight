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
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class RunningLight extends Animation {
	
	private Color[] color;
	private int pass, counter;

	public RunningLight() {
		super("RunningLight");
		this.addSetting(new SettingBoolean("animation.runninglight.randomcolor", "Random color", SettingCategory.Intern, null, true));
		this.addSetting(new SettingColor("animation.runninglight.color", "Color", SettingCategory.Intern,	null, Color.RED));
	}
	
	@Override
	public void onEnable() {
	    this.color = new Color[] {Color.RED, Color.ORANGE, Color.PINK, Color.MAGENTA,
	    		Color.BLUE, Color.CYAN, Color.GREEN};
	    pass = 0; counter = 0;
	    
	    for(int i = 0; i < Main.getLedNum(); i++) {
	    	onLoop();
	    }
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		if(counter >= color.length) {
			counter = 0;
		}
		Color c = color[counter];
		
		if(!((SettingBoolean) getSetting("animation.runninglight.randomcolor")).getValue()) {
			c = ((SettingColor) getSetting("animation.runninglight.color")).getValue();
		}
		
		if(pass < 5) {
			switch (pass) {
			case 0:
				PixelColorUtils.setPixel(0, c.darker().darker());
				break;
			case 1:
				PixelColorUtils.setPixel(0, c.darker());
				break;
			case 2:
				PixelColorUtils.setPixel(0, c);
				break;
			case 3:
				PixelColorUtils.setPixel(0, c.darker());
				break;
			case 4:
				PixelColorUtils.setPixel(0, c.darker().darker());
				counter++;
				break;
			}
			
			pass++;
		} else if(pass < 10) {
			pass++;
			PixelColorUtils.setPixel(0, Color.BLACK);
		} else {
			pass = 0;
			PixelColorUtils.setPixel(0, Color.BLACK);
		}
		
		//shift pixels to right
		PixelColorUtils.shiftRight(1);
		
		super.onLoop();
	}

}
