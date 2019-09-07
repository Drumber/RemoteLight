package de.lars.remotelightclient.utils;

import java.awt.Color;

public class PixelColorUtils {
	
	public static Color[] colorAllPixels(Color color, int size) {
		Color[] pixels = new Color[size];
		for(int i = 0; i < size; i++) {
			pixels[i] = color;
		}
		return pixels;
	}
	
	/**
	 * 
	 * @param colors Color array
	 * @param value Dim value between 0 and 100
	 */
	public static void changeBrightness(Color[] colors, int value) {
		if(value < 0) {
			value = 0;
		} else if(value > 100) {
			value = 100;
		}
		for(int i = 0; i < colors.length; i++) {
			int r = colors[i].getRed() * value / 100;
			int g = colors[i].getGreen() * value / 100;
			int b = colors[i].getBlue() * value / 100;
			
			colors[i] = new Color(r, g, b);
		}
	}

}
