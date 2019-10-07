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
package de.lars.remotelightclient.utils;

import java.awt.Color;

/*
 * adapted from https://stackoverflow.com/a/52498075
 */

public class RainbowWheel {
	
	private final static int SIZE = 360;
	private static Color[] rainbow = new Color[SIZE];
	
	public static void init() {
		double jump = 1.0 / (SIZE * 1.0);
		int[] colors = new int[SIZE];
		for(int i = 0; i < colors.length; i++) {
			colors[i] = Color.HSBtoRGB((float) (jump * i), 1.0f, 1.0f);
			rainbow[i] = Color.getHSBColor((float) (jump * i), 1.0f, 1.0f);
		}
	}
	
	public static Color[] getRainbow() {
		return rainbow;
	}

}
