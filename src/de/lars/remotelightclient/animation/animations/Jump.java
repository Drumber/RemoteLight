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
import de.lars.remotelightclient.utils.PixelColorUtils;
import de.lars.remotelightclient.utils.RainbowWheel;
import de.lars.remotelightclient.utils.TimeUtil;

public class Jump extends Animation {
	
	private TimeUtil time;

	public Jump() {
		super("Jump");
	}
	
	@Override
	public void onEnable() {
		time = new TimeUtil(Main.getInstance().getAnimationManager().getDelay()*3);
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		time.setInterval(Main.getInstance().getAnimationManager().getDelay()*3);
		
		if(time.hasReached()) {
			Color color = RainbowWheel.getRandomColor();
			OutputManager.addToOutput(PixelColorUtils.colorAllPixels(color, Main.getLedNum()));
		}
		super.onLoop();
	}

}
