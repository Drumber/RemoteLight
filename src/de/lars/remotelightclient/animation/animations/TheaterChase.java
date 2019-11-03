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
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.utils.RainbowWheel;

public class TheaterChase extends Animation {
	
	private boolean swtch;
	private int hsv = 0;
	private int counter = 0;
	private Color[] strip;

	public TheaterChase() {
		super("TheaterChase");
	}
	
	@Override
	public void onEnable() {
		strip = new Color[Main.getLedNum()];
		for(int i = 0; i < strip.length; i++) {
			strip[i] = Color.BLACK;
		}
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		if(swtch) {
			
			for(int i = strip.length-1; i > 0; i--) {
				strip[i] = strip[i-1];
			}
			
			if(counter++ == 2) {
				strip[0] = RainbowWheel.getRainbow()[hsv];
				counter = 0;
			} else {
				strip[0] = Color.BLACK;
			}
			
			hsv++;
			if(hsv >= RainbowWheel.getRainbow().length) {
				hsv = 0;
			}
			
			OutputManager.addToOutput(strip);
		}
		swtch = !swtch;
		
		super.onLoop();
	}
	
}
