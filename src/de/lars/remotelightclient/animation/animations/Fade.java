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
import de.lars.remotelightclient.utils.ColorUtil;
import de.lars.remotelightclient.utils.PixelColorUtils;
import de.lars.remotelightclient.utils.RainbowWheel;

public class Fade extends Animation {

	private Color color = Color.RED;
	private int dimVal = 100;

	public Fade() {
		super("Fade");
	}

	@Override
	public void onLoop() {
		if (dimVal <= 1) {
			color = RainbowWheel.getRainbow()[new Random().nextInt(RainbowWheel.getRainbow().length)];
			dimVal = 100;
		}
		dimVal--;

		Color c = ColorUtil.dimColor(color, dimVal);
		OutputManager.addToOutput(PixelColorUtils.colorAllPixels(c, Main.getLedNum()));

		super.onLoop();
	}

}
