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
import de.lars.remotelightcore.utils.color.ColorUtil;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class Breath extends Animation {
	
	private int brghtns = 100;
	private Color color;
	private boolean darker = true;
	private int nxtColorCounter = 0;

	public Breath() {
		super("Breath");
		color = RainbowWheel.getRandomColor();
		this.addSetting(new SettingBoolean("animation.breath.randomcolor", "Random color", SettingCategory.Intern, null, true));
		this.addSetting(new SettingColor("animation.breath.color", "Color", SettingCategory.Intern,	null, Color.RED));
	}
	
	@Override
	public void onLoop() {
		if(!((SettingBoolean) getSetting("animation.breath.randomcolor")).getValue()) {
			color = ((SettingColor) getSetting("animation.breath.color")).getValue();
		}
		Color tmp = color;
		
		if(darker) {
			if(--brghtns <= 5) {
				darker = false;
			}
		} else {
			if(++brghtns >= 100) {
				darker = true;
				if(++nxtColorCounter == 2) {
					color = RainbowWheel.getRandomColor();
					nxtColorCounter = 0;
				}
			}
		}
		tmp = ColorUtil.dimColor(tmp, brghtns);
		OutputManager.addToOutput(PixelColorUtils.colorAllPixels(tmp, Main.getLedNum()));
		super.onLoop();
	}

}
