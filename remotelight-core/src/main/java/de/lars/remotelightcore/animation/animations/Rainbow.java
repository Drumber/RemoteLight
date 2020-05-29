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

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;

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
			for(int i = 0; i < RemoteLightCore.getLedNum(); i++) {
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
			OutputManager.addToOutput(PixelColorUtils.colorAllPixels(RainbowWheel.getRainbow()[step], RemoteLightCore.getLedNum()));
			step++;
		}
		super.onLoop();
	}

}
