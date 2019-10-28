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

public class RandomColor2 extends Animation {

	public RandomColor2() {
		super("RandomColor#2");
	}
	
	@Override
	public void onLoop() {
		int rnd = new Random().nextInt(Main.getLedNum());
		
		PixelColorUtils.setPixel(rnd, rndmColor());
		
		super.onLoop();
	}
	
	private Color rndmColor() {
		return RainbowWheel.getRainbow()[new Random().nextInt(RainbowWheel.getRainbow().length)];
	}

}
