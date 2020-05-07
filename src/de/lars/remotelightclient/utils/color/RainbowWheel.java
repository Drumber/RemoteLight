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
package de.lars.remotelightclient.utils.color;

import java.awt.Color;
import java.util.Random;

/*
 * adapted from https://stackoverflow.com/a/52498075
 */

public class RainbowWheel {
	/**
	 * Length of the rainbow array
	 */
	private final static int SIZE = 360;
	private static Color[] rainbow = new Color[SIZE];
	
	public static void init() {
		double jump = 1.0 / (SIZE * 1.0);
		for(int i = 0; i < SIZE; i++) {
			rainbow[i] = Color.getHSBColor((float) (jump * i), 1.0f, 1.0f);
		}
	}
	
	/**
	 * 
	 * @return color array from 0 to 360(excluded)
	 */
	public static Color[] getRainbow() {
		return rainbow;
	}

	/**
	 * 
	 * @return A random color from the rainbow array
	 */
	public static Color getRandomColor() {
		float random = new Random().nextFloat();
		return Color.getHSBColor(random, 1.0f, 1.0f);
	}
	
}
