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
import de.lars.remotelightclient.utils.PixelColorUtils;

public class Wipe extends Animation {
	
	private Color[] colors;
	Color c;
	Color[] strip;
	int pix, count;
	boolean wiping;

	public Wipe() {
		super("Wipe");
	}
	
	@Override
	public void onEnable() {
		Color[] colors = {Color.RED, Color.ORANGE, Color.PINK, Color.MAGENTA,
	    		Color.BLUE, Color.CYAN, Color.GREEN};
		this.colors = colors;
		c = Color.RED;
		strip = Main.getInstance().getOutputManager().getLastColors();
		pix = 0; count = 0;
		wiping = false;
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		if(!wiping) {
			count++;
			if(count >= colors.length) count = 0;
			c = colors[count];
			PixelColorUtils.setPixel(0, c);
			pix = 0;
			wiping = true;
		} else {
			pix++;
			if(pix < strip.length) {
				PixelColorUtils.setPixel(pix, c);
			} else {
				wiping = false;
			}
		}
		super.onLoop();
	}

}
