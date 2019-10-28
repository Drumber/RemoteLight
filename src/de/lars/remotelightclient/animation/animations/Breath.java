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
import de.lars.remotelightclient.utils.ColorUtil;
import de.lars.remotelightclient.utils.PixelColorUtils;
import de.lars.remotelightclient.utils.RainbowWheel;

public class Breath extends Animation {
	
	private int brghtns = 100;
	private Color color;
	private boolean darker = true;
	private int nxtColorCounter = 0;

	public Breath() {
		super("Breath");
		color = RainbowWheel.getRandomColor();
	}
	
	@Override
	public void onLoop() {
		Color tmp = color;
		
		if(darker) {
			if(--brghtns <= 5) {
				darker = false;
			}
		} else {
			if(++brghtns >= 100) {
				darker = true;
				if(++nxtColorCounter == 3) {
					color = RainbowWheel.getRandomColor();
					nxtColorCounter = 0;
				}
			}
		}
		tmp = ColorUtil.dimColor(tmp, brghtns);
		OutputManager.addToOutput(PixelColorUtils.colorAllPixels(tmp, Main.getLedNum()));
		super.onLoop();
	}

}
