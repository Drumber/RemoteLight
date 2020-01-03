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

import de.lars.remotelightclient.devices.arduino.RgbOrder;

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
	
	
	public static boolean similar(Color c1, Color c2) {
		int deltaR = c1.getRed() - c2.getRed();
		int deltaG = c1.getGreen() - c2.getGreen();
		int deltaB = c1.getBlue() - c2.getBlue();
		
		double distance = deltaR * deltaR + deltaG * deltaG + deltaB * deltaB;
		return distance < 50000;
	}
	
	
	/**
	 * Fade from one to another color
	 * @param oldColor old color
	 * @param newColor new color
	 * @param step step between 0.0f and 1.0f
	 * @return the faded color for step x
	 */
	public static Color fadeToColor(Color oldColor, Color newColor, float step) {
		float inverseStep = 1 - step;
		float red = newColor.getRed() * step + oldColor.getRed() * inverseStep;
		float green = newColor.getGreen() * step + oldColor.getGreen() * inverseStep;
		float blue = newColor.getBlue() * step + oldColor.getBlue() * inverseStep;
		red /= 255;
		green /= 255;
		blue /= 255;
		if(red >= 1) red = 1;
		if(green >= 1) green = 1;
		if(blue >= 1) blue = 1;
		//System.out.println(step + " : " + red + "/" + green + "/" + blue);
		return new Color(red, green, blue);
	}
	
	
	/**
	 * Return a new color with swapped RGB order
	 * @param c Input color
	 * @param order RGB order
	 * @return Out color with swapped RGB order
	 */
	public static Color matchRgbOrder(Color c, RgbOrder order) {
		Color out;
		switch (order) {
		
		case RGB:
			out = c;
			break;
		case RBG:
			out = new Color(c.getRed(), c.getBlue(), c.getGreen());
			break;
		case GRB:
			out = new Color(c.getGreen(), c.getRed(), c.getBlue());
			break;
		case GBR:
			out = new Color(c.getGreen(), c.getBlue(), c.getRed());
			break;
		case BRG:
			out = new Color(c.getBlue(), c.getRed(), c.getGreen());
			break;
		case BGR:
			out = new Color(c.getBlue(), c.getGreen(), c.getRed());
			break;

		default:
			out = c;
			break;
		}
		return out;
	}

}
