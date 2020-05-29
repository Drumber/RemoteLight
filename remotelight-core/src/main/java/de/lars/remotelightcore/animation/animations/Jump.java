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
package de.lars.remotelightcore.animation.animations;

import java.awt.Color;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;
import de.lars.remotelightcore.utils.maths.TimeUtil;

public class Jump extends Animation {
	
	private TimeUtil time;

	public Jump() {
		super("Jump");
	}
	
	@Override
	public void onEnable() {
		time = new TimeUtil(RemoteLightCore.getInstance().getAnimationManager().getDelay()*3);
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		time.setInterval(RemoteLightCore.getInstance().getAnimationManager().getDelay()*3);
		
		if(time.hasReached()) {
			Color color = RainbowWheel.getRandomColor();
			OutputManager.addToOutput(PixelColorUtils.colorAllPixels(color, RemoteLightCore.getLedNum()));
		}
		super.onLoop();
	}

}
