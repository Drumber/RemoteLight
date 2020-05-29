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
package de.lars.remotelightcore.utils.color;

import java.awt.Color;
import java.util.HashMap;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.out.OutputManager;

public class PixelColorUtils {
	
	/**
	 * 
	 * @param color The color you want to apply to the whole array
	 * @param size Size of the array
	 * @return Color array of the defined color
	 */
	public static Color[] colorAllPixels(Color color, int size) {
		Color[] pixels = new Color[size];
		for(int i = 0; i < size; i++) {
			pixels[i] = color;
		}
		return pixels;
	}
	
	/**
	 * Set all pixels to black
	 */
	public static void setAllPixelsBlack() {
		OutputManager.addToOutput(colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum()));
	}
	
	/**
	 * @param amount Number of places you want to move the LEDs
	 */
	public static void shiftRight(int amount) {
		Color[] strip = RemoteLightCore.getInstance().getOutputManager().getLastColors();
		Color[] leds = strip;
		for(int r = 0; r < amount; r++) {
			for(int i = 1; i <= strip.length; i++) {
				if(i == strip.length) {
					leds[0] = strip[0];
				} else {
					leds[strip.length - i] = strip[strip.length - i - 1];
				}
			}
		}
		OutputManager.addToOutput(leds);
	}
	
	/**
	 * @param amount Number of places you want to move the LEDs
	 */
	public static void shiftLeft(int amount) {
		Color[] strip = RemoteLightCore.getInstance().getOutputManager().getLastColors();
		Color[] leds = strip;
		for(int r = 0; r < amount; r++) {
			for(int i = 0; i < strip.length; i++) {
				if(i == strip.length - 1) {
					leds[strip.length - 1] = strip[strip.length - 1];
				} else {
					leds[i] = strip[i + 1];
				}
			}
		}
		OutputManager.addToOutput(leds);
	}
	
	/**
	 * Shift pixels outwards from the center
	 * @param amount Number of places you want to move the LEDs
	 */
	public static void shiftCenter(int amount) {
		Color[] strip = RemoteLightCore.getInstance().getOutputManager().getLastColors();
		Color[] leds = strip;
		for(int r = 0; r < amount; r++) {
			for(int i = (strip.length - 1); i > (strip.length/2); i--) {	//shift to the right
				leds[i] = strip[i - 1];
			}
			
			for(int i = 0; i < (strip.length/2); i++) {						//shift to the left
				leds[i] = strip[i + 1];
			}
		}
	}
	
	/**
	 * Sets the color of a single pixel/LED
	 *
	 */
	public static void setPixel(int pixel, Color color) {
		Color[] strip = RemoteLightCore.getInstance().getOutputManager().getLastColors();
		if(strip.length < 1) {
			strip = colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum());
		}
		Color[] leds = strip;
		for(int i = 0; i < RemoteLightCore.getLedNum(); i++) {
			if(i == pixel) {
				leds[i] = color;
				
			} else {
				leds[i] = strip[i];
			}
		}
		OutputManager.addToOutput(leds);
	}
	
	public static Color[] pixelHashToColorArray(HashMap<Integer, Color> pixelHash) {
		Color[] leds = new Color[RemoteLightCore.getLedNum()];
		for(int i = 0; i < RemoteLightCore.getLedNum(); i++) {
			if(pixelHash.get(i) == null) {
				leds[i] = Color.BLACK;
			} else {
				leds[i] = pixelHash.get(i);
			}
		}
		return leds;
	}
	
	/**
	 * 
	 * @param right Flip the right side to the left if true
	 */
	public static Color[] centered(Color[] pixels, boolean right) {
		int half = pixels.length / 2;
		Color[] strip = pixels;
		
		if(right) {
			for(int i = 0; i < half; i++) {
				strip[i] = pixels[pixels.length - 1 - i];
			}
		}
		else {
			for(int i = 0; i < half; i++) {
				strip[half + i] = pixels[half - 1 - i];
			}
		}
		return strip;
	}

}
