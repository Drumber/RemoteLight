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
package de.lars.remotelightclient.scene.scenes;

import java.awt.Color;
import java.util.HashMap;
import java.util.Random;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.scene.Scene;
import de.lars.remotelightclient.utils.color.PixelColorUtils;

public class Space extends Scene {
	
	private int pix;
	private HashMap<Integer, Integer> stars;  //ledNum, Brightness
	private Random r;

	public Space() {
		super("Space", 50);
	}
	
	@Override
	public void onEnable() {
		pix = Main.getLedNum();
		stars = new HashMap<>();
		r = new Random();
		OutputManager.addToOutput(PixelColorUtils.colorAllPixels(Color.BLACK, pix));
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		if(stars.size() < ((pix / 4) + r.nextInt(10))) {
			int led = r.nextInt(pix);
			int brightness = r.nextInt(200) + 56;
			
			stars.put(led, brightness);
		}
		
		for(int i = 0; i < pix; i++) {
			if(stars.containsKey(i)) {
				
				int b = stars.get(i);
				PixelColorUtils.setPixel(i, new Color(b, b , (b / 2)));
				
				b -= 5;
				if(b <= 0) {
					PixelColorUtils.setPixel(i, Color.BLACK);
					stars.remove(i);
				} else {
					stars.put(i, b);
				}
			}
		}
		super.onLoop();
	}

}
