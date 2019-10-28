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
import java.util.ArrayList;
import java.util.List;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.scene.Scene;
import de.lars.remotelightclient.utils.PixelColorUtils;

public class Sunset extends Scene {
	
	private Color[] sun;
	private int count;

	public Sunset() {
		super("Sunset", 150);
	}
	
	@Override
	public void onEnable() {
		count = 0;
		initSun();
		
		for(int i = 0; i < Main.getLedNum(); i++) {
			PixelColorUtils.shiftRight(1);
			count++;
			if(count >= sun.length)
				count = 0;
			
			PixelColorUtils.setPixel(0, sun[count]);
		}
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		PixelColorUtils.shiftRight(1);
		
		count++;
		if(count >= sun.length)
			count = 0;
		
		PixelColorUtils.setPixel(0, sun[count]);
		
		super.onLoop();
	}
	
	
	private void initSun() {
		List<Color> colors = new ArrayList<Color>();
		
		for(int i = 0; i < 106; i += 5)
			colors.add(new Color(150 + i, 0, 0));
		
		colors.add(new Color(255, 0, 0));
		for(int i = 0; i < 160; i++)
			colors.add(new Color(255, i, 0));
	
		for(int i = 160; i > 0; i--)
			colors.add(new Color(255, i, 0));
		colors.add(new Color(255, 0, 0));
		
		for(int i = 0; i < 106; i += 5)
			colors.add(new Color(255 - i, 0, 0));
		
		sun = colors.toArray(new Color[colors.size()]);
	}

}
