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

}
