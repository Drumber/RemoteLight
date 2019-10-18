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
package de.lars.remotelightclient.musicsync.modes;

import java.awt.Color;

import de.lars.remotelightclient.musicsync.MusicEffect;
import de.lars.remotelightclient.utils.PixelColorUtils;
import de.lars.remotelightclient.utils.RainbowWheel;
import de.lars.remotelightclient.utils.TimeUtil;

public class RunningLight extends MusicEffect {
	
	private double lastTime = 0;
	private final double multiplier = 0.2;
	private int delay = 0;

	public RunningLight() {
		super("RunningLight");
	}
	
	@Override
	public void onLoop() {
		if(++delay < 2) {
			return;
		}
		delay = 0;
		double pitch = this.getPitch();
		double time = this.getPitchTime();
		
		PixelColorUtils.shiftRight(3);
		
		if(time != lastTime) {
			lastTime = time;
			int r = 0, g = 0, b = 0;
			
			if(pitch < 50) {
				r = 180;
			} else if(pitch < 200) {
				r = 250;
				g = 20;
			} else if(pitch < 400) {
				r = 50; g = 250;
			} else if(pitch < 800) {
				g = 250;
			} else if(pitch < 1000) {
				g = 250;
				b = 50;
			} else if(pitch < 1600) {
				b = 250;
			} else {
				b = 250;
				g = 150;
			}
			
			for(int i = 0; i < 3; i++) {
				PixelColorUtils.setPixel(i, getColor(pitch));
			}
		} else {
			for(int i = 0; i < 3; i++) {
				PixelColorUtils.setPixel(i, Color.BLACK);
			}
		}
		super.onLoop();
	}
	
	private Color getColor(double pitch) {
		int value = (int) (multiplier * pitch);
		if(value >= RainbowWheel.getRainbow().length) {
			value = RainbowWheel.getRainbow().length - 1;
		}
		if(value < 0) {
			value = 0;
		}
		//show more red colors
		if(pitch < 60) {
			value /= 2;
		}
		
		return RainbowWheel.getRainbow()[value];
	}

}
