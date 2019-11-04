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

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.animation.Animation;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;
import de.lars.remotelightclient.settings.types.SettingBoolean;
import de.lars.remotelightclient.utils.PixelColorUtils;
import de.lars.remotelightclient.utils.RainbowWheel;

public class Rainbow extends Animation {
	
	private int step;

	public Rainbow() {
		super("Rainbow");
		this.addSetting(new SettingBoolean("animation.rainbow.cycle", "Cycle", SettingCategory.Intern, null, true));
	}
	
	@Override
	public void onEnable() {
		step = 0;
		if(((SettingBoolean) getSetting("animation.rainbow.cycle")).getValue()) {
			for(int i = 0; i < Main.getLedNum(); i++) {
				PixelColorUtils.shiftRight(1);
				step += 3;
				if(step >= RainbowWheel.getRainbow().length) {
					step = 0;
				}
				PixelColorUtils.setPixel(0, RainbowWheel.getRainbow()[step]);
			}
		}
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		if(step >= RainbowWheel.getRainbow().length) {
			step = 0;
		}
		
		if(((SettingBoolean) getSetting("animation.rainbow.cycle")).getValue()) {
			PixelColorUtils.shiftRight(1);
			PixelColorUtils.setPixel(0, RainbowWheel.getRainbow()[step]);
			step += 3;
		} else {
			OutputManager.addToOutput(PixelColorUtils.colorAllPixels(RainbowWheel.getRainbow()[step], Main.getLedNum()));
			step++;
		}
		super.onLoop();
	}

}
