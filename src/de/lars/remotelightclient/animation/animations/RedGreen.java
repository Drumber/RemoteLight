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

import de.lars.remotelightclient.animation.Animation;
import de.lars.remotelightclient.utils.PixelColorUtils;

public class RedGreen extends Animation {
	
	private int colorCounter = 0;
	private boolean red = true;

	public RedGreen() {
		super("Red & Green");
	}
	
	@Override
	public void onLoop() {
		PixelColorUtils.shiftRight(1);
		
		if(++colorCounter == 2) {
			colorCounter = 0;
			red = !red;
		}
		
		Color c;
		if(red) {
			c = Color.RED;
		} else {
			c = Color.GREEN;
		}
		
		PixelColorUtils.setPixel(0, c);
		super.onLoop();
	}

}
