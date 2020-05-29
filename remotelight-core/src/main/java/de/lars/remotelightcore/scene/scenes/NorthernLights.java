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
package de.lars.remotelightcore.scene.scenes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.scene.Scene;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class NorthernLights extends Scene {
	
	private Color[] colors;
	private int pix;
	private int count;
	private Color[] strip;
	private Random r;
	private boolean newColor;
	private HashMap<Integer, NorthernLightTrail> lights;

	public NorthernLights() {
		super("NorthernLight", 100);
	}
	
	@Override
	public void onEnable() {
		Color[] colors = {new Color(0, 207, 82), new Color(3, 46, 62), new Color(25, 100, 106), new Color(0, 198, 144),
				new Color(0, 223, 150), new Color(142, 0, 251)};
		this.colors = colors;
		pix = RemoteLightCore.getLedNum();
		count = 0;
		
		strip = new Color[pix];
		strip = this.initStrip(strip);
		
		r = new Random();
		newColor = true;
		lights = new HashMap<>();
		
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		count = lights.size();
		
		if(count < (pix / 20))
			newColor = true;
		else
			newColor = false;
		
		if(newColor) {
			int ranColor = r.nextInt(colors.length);
			int startPix = r.nextInt(pix);
			int direction = r.nextInt(2);
			
			boolean right;
			
			if(direction == 0) right = true;
			else right = false;
			
			
			lights.put(startPix, new NorthernLightTrail(startPix, colors[ranColor], right));
		}
		
		//dim all
		for(int i = 0; i < pix; i++) {
			int red = strip[i].getRed() - (strip[i].getRed() / 8);
			int green = strip[i].getGreen() - (strip[i].getGreen() / 8);
			int blue = strip[i].getBlue() - (strip[i].getBlue() / 8);
			
			if(red < 10 && green < 10 && blue < 10)
				strip[i] = Color.BLACK;
			else
				strip[i] = new Color(red, green, blue);
		}
		
		//set pixel color
		HashMap<Integer, Color> pixelHash = new HashMap<>();
		List<Integer> alreadyPut = new ArrayList<>();
		
		for(int i = 0; i < pix; i++) {
			
			if(lights.containsKey(i) && !alreadyPut.contains(i)) {
				
				NorthernLightTrail led = lights.get(i);
				
				pixelHash.put(i, led.color);
				strip[i] = led.color;
				
				if(led.right) {
					
					if(i != (pix - 1)) {
						lights.put((i + 1), led);
						alreadyPut.add((i + 1));
						
					}
					
					lights.remove(i);
					
				} else {
					
					if(i != 0) {
						lights.put((i - 1), led);
						alreadyPut.add((i - 1));
						
					}
					
					lights.remove(i);
				}
			} else {
				pixelHash.put(i, strip[i]);
			}
		}
		
		OutputManager.addToOutput(PixelColorUtils.pixelHashToColorArray(pixelHash));
		super.onLoop();
	}
	
	
	private Color[] initStrip(Color[] strip) {
		for(int i = 0; i < strip.length; i++)
			strip[i] = Color.BLACK;
		return strip;
	}

}
