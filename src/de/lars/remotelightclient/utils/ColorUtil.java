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

public class ColorUtil {
	
	/**
	 * @param color Color to dim
	 * @param value Dim value between 0 and 100
	 */
	public static Color dimColor(Color color, int value) {
		if(value < 0) {
			value = 0;
		} else if(value > 100) {
			value = 100;
		}
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		
		r = r * value / 100;
		g = g * value / 100;
		b = b * value / 100;
		
		return new Color(r, g, b);
	}
	
	public static int getAvgRgbValue(Color color) {
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		
		return (r + g + b) / 3;
	}

}
