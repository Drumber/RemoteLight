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
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.utils.RainbowWheel;

public class Meteor extends Animation {
	
	private int pos = 0;
	private boolean right = true;
	private Color color;

	public Meteor() {
		super("Meteor");
		rndmColor();
	}
	
	@Override
	public void onLoop() {
		Color[] strip = Main.getInstance().getOutputManager().getLastColors();
		
		for(int i = 0; i < Main.getLedNum(); i++) {
			strip[i] = dim(strip[i]);
		}
		strip[pos] = color;
		
		if(right) {
			if(++pos == Main.getLedNum()) {
				pos = Main.getLedNum()-1;
				right = false;
			}
		}
		else {
			if(--pos < 0) {
				pos = 0;
				right = true;
				rndmColor();
			}
		}
		OutputManager.addToOutput(strip);
		
		super.onLoop();
	}
	
	private void rndmColor() {
		color = RainbowWheel.getRainbow()[new Random().nextInt(RainbowWheel.getRainbow().length)];
	}
	
	private Color dim(Color c) {
		int r = c.getRed();
		r /= 1.5;
		int g = c.getGreen();
		g /= 1.5;
		int b = c.getBlue();
		b /= 1.5;
		
		if(r < 2) r = 0;
		if(g < 2) g = 0;
		if(b < 2) b = 0;
		
		return new Color(r, g, b);
	}

}
