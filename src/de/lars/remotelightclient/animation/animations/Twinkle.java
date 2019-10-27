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
import de.lars.remotelightclient.animation.AnimationManager;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.utils.PixelColorUtils;
import de.lars.remotelightclient.utils.TimeUtil;

public class Twinkle extends Animation {
	
	private AnimationManager am;
	private int max;
	private Color color;
	private TimeUtil time;

	public Twinkle() {
		super("Twinkle");
	}
	
	@Override
	public void onEnable() {
		am = Main.getInstance().getAnimationManager();
		max = Main.getLedNum() / 10;
		color = new Color(255, 240, 255);
		time = new TimeUtil(am.getDelay());
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		if(time.hasReached()) {
			OutputManager.addToOutput(PixelColorUtils.colorAllPixels(Color.BLACK, Main.getLedNum()));
			for(int i = 0; i <= max; i++) {
				if(new Random().nextInt(3) == 0) {
					PixelColorUtils.setPixel(new Random().nextInt(Main.getLedNum()), color);
				}
			}
			
			int rnd = (am.getDelay() * 2) + new Random().nextInt(50);
			time.setInterval(rnd);
		}
		
		super.onLoop();
	}

}
