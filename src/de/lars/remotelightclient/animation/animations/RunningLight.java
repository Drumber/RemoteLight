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

public class RunningLight extends Animation {
	
	private Color[] color;
	private int pass, counter;

	public RunningLight() {
		super("RunningLight");
	}
	
	@Override
	public void onEnable() {
	    Color[] color = {Color.RED, Color.ORANGE, Color.PINK, Color.MAGENTA,
	    		Color.BLUE, Color.CYAN, Color.GREEN};
	    this.color = color;
	    pass = 0; counter = 0;
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		if(pass < 5) {
			switch (pass) {
			case 0:
				if(counter >= color.length) counter = 0;
				PixelColorUtils.setPixel(0, color[counter].darker().darker());
				break;
			case 1:
				if(counter > color.length) counter = 0;
				PixelColorUtils.setPixel(0, color[counter].darker());
				break;
			case 2:
				if(counter > color.length) counter = 0;
				PixelColorUtils.setPixel(0, color[counter]);
				break;
			case 3:
				if(counter > color.length) counter = 0;
				PixelColorUtils.setPixel(0, color[counter].darker());
				break;
			case 4:
				PixelColorUtils.setPixel(0, color[counter].darker().darker());
				counter++;
				if(counter >= color.length) counter = 0;
				break;
				
			default:
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
