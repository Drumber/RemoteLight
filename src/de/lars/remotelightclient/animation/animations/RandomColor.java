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
import java.util.Random;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.animation.Animation;
import de.lars.remotelightclient.utils.PixelColorUtils;
import de.lars.remotelightclient.utils.RainbowWheel;

public class RandomColor extends Animation {
	
	private int pos;

	public RandomColor() {
		super("RandomColor#1");
	}
	
	@Override
	public void onLoop() {
		PixelColorUtils.setPixel(pos, rndmColor());
		
		if(++pos >= Main.getLedNum()) {
			pos = 0;
		}
		super.onLoop();
	}
	
	private Color rndmColor() {
		return RainbowWheel.getRainbow()[new Random().nextInt(RainbowWheel.getRainbow().length)];
	}

}
