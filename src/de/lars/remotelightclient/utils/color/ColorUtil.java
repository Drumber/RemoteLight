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

import de.lars.remotelightclient.devices.arduino.RgbOrder;

public class ColorUtil {
	
	/**
	 * @param color Color to dim
	 * @param value Dim value between 0 and 100 (smaller = darker)
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
	
	/**
	 * @param color Input color
	 * @param subtract Value between 0 and 255
	 * @return Color - subtract
	 */
	public static Color dimColorSimple(Color color, int subtract) {
		if(subtract > 255) subtract = 255;
		if(subtract < 0) subtract = 0;
		int r = color.getRed() - subtract;
		int g = color.getGreen() - subtract;
		int b = color.getBlue() - subtract;
		if(r < 0) r = 0;
		if(g < 0) g = 0;
		if(b < 0) b = 0;
		return new Color(r, g, b);
	}
	
	/**
	 * @param strip Color array
	 * @param subtract Value between 0 and 255
	 * @return Every color of array - subtract
	 */
	public static Color[] dimColorSimple(Color[] strip, int subtract) {
		if(subtract > 255) subtract = 255;
		if(subtract < 0) subtract = 0;
		for(int i = 0; i < strip.length; i++) {
			Color c = strip[i];
			int r = c.getRed() - subtract;
			int g = c.getGreen() - subtract;
			int b = c.getBlue() - subtract;
			if(r < 0) r = 0;
			if(g < 0) g = 0;
			if(b < 0) b = 0;
			strip[i] = new Color(r, g, b);
		}
		return strip;
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
	 * Mix two colors
	 * @param oldColor Color 1
	 * @param newColor Color 2
	 * @return new color
	 */
	public static Color mixColor(Color oldColor, Color newColor) {
		double alpha = oldColor.getAlpha() + newColor.getAlpha();
		double weightOld = oldColor.getAlpha() / alpha;
		double weightNew = newColor.getAlpha() / alpha;
		
		int r = (int) (weightOld * oldColor.getRed() + weightNew * newColor.getRed());
		int g = (int) (weightOld * oldColor.getGreen() + weightNew * newColor.getGreen());
		int b = (int) (weightOld * oldColor.getBlue() + weightNew * newColor.getBlue());
		return new Color(r, g, b);
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
	
	
	/**
	 * Convert audio tones to the visible spectrum of light
	 * <br> Source:
	 * <br>https://roelhollander.eu/tuning-frequency/sound-light-colour
	 * @param h Hertz
	 * @return Color
	 */
	public static Color soundToColor(int h) {
		if(h < 392)
			return new Color(140, 0, 2);
		if(h < 397)
			return new Color(213, 3, 6);
		if(h < 431)
			return new Color(243, 123, 0);
		if(h < 440)
			return new Color(248, 148, 0);
		if(h < 464)
			return new Color(238, 250, 0);
		if(h < 494)
			return new Color(144, 230, 19);
		if(h < 497)
			return new Color(124, 227, 17);
		if(h < 523)
			return new Color(66, 211, 47);
		if(h < 531)
			return new Color(42, 192, 67);
		if(h < 565)
			return new Color(2, 156, 157);
		if(h < 587)
			return new Color(1, 73, 212);
		if(h < 598)
			return new Color(2, 8, 251);
		if(h < 632)
			return new Color(69, 0, 220);
		if(h < 659)
			return new Color(79, 0, 217);
		if(h < 665)
			return new Color(73, 3, 193);
		if(h < 698)
			return new Color(43, 1, 107);
		
		return new Color(21, 0, 60);
	}

}
