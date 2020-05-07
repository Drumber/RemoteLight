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
import de.lars.remotelightclient.utils.color.ColorUtil;
import de.lars.remotelightclient.utils.color.PixelColorUtils;
import de.lars.remotelightclient.utils.color.RainbowWheel;

public class Fade extends Animation {

	private Color color = Color.RED;
	private int dimVal = 100;

	public Fade() {
		super("Fade");
		this.addSetting(new SettingBoolean("animation.fade.randomcolor", "Random color", SettingCategory.Intern, null, true));
		this.addSetting(new SettingColor("animation.fade.color", "Color", SettingCategory.Intern,	null, Color.RED));
	}

	@Override
	public void onLoop() {
		if (dimVal <= 1) {
			color = RainbowWheel.getRandomColor();
			dimVal = 100;
		}
		dimVal--;
		
		if(!((SettingBoolean) getSetting("animation.fade.randomcolor")).getValue()) {
			color = ((SettingColor) getSetting("animation.fade.color")).getValue();
		}

		Color c = ColorUtil.dimColor(color, dimVal);
		OutputManager.addToOutput(PixelColorUtils.colorAllPixels(c, Main.getLedNum()));

		super.onLoop();
	}

}
