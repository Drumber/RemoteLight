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

import java.util.Random;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.animation.Animation;
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;
import de.lars.remotelightclient.settings.types.SettingSelection;
import de.lars.remotelightclient.utils.PixelColorUtils;
import de.lars.remotelightclient.utils.RainbowWheel;

public class RandomColor extends Animation {
	
	private int pos;
	private String lastMode;

	public RandomColor() {
		super("RandomColor");
		this.addSetting(new SettingSelection("animation.randomcolor.mode", "Mode", SettingCategory.Intern, "",
				new String[] {"Mode 1", "Mode 2"}, "Mode 1", SettingSelection.Model.ComboBox));
		lastMode = ((SettingSelection) this.getSetting("animation.randomcolor.mode")).getSelected();
	}
	
	@Override
	public void onLoop() {
		String mode = ((SettingSelection) this.getSetting("animation.randomcolor.mode")).getSelected();
		if(!mode.equals(lastMode)) {
			PixelColorUtils.setAllPixelsBlack();
			lastMode = mode;
			pos = 0;
		}
		
		if(mode.equals("Mode 1")) {
			mode1();
		} else if(mode.equals("Mode 2")) {
			mode2();
		}
		super.onLoop();
	}
	
	private void mode1() {
		PixelColorUtils.setPixel(pos, RainbowWheel.getRandomColor());
		if(++pos >= Main.getLedNum()) {
			pos = 0;
		}
	}
	
	private void mode2() {
		int rnd = new Random().nextInt(Main.getLedNum());
		
		PixelColorUtils.setPixel(rnd, RainbowWheel.getRandomColor());
	}

}
