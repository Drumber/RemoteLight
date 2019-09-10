package de.lars.remotelightclient.utils;

import java.awt.Color;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.out.OutputManager;

public class PixelColorUtils {
	
	public static Color[] colorAllPixels(Color color, int size) {
		Color[] pixels = new Color[size];
		for(int i = 0; i < size; i++) {
			pixels[i] = color;
		}
		return pixels;
	}
	
	public static void shiftRight(int amount) {
		Color[] strip = Main.getInstance().getOutputManager().getLastColors();
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
	
	public static void shiftLeft(int amount) {
		Color[] strip = Main.getInstance().getOutputManager().getLastColors();
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
	
	public static void setPixel(int pixel, Color color) {
		Color[] strip = Main.getInstance().getOutputManager().getLastColors();
		Color[] leds = strip;
		for(int i = 0; i < Main.getLedNum(); i++) {
			if(i == pixel)
				leds[i] = color;
			else
				leds[i] = strip[i];
		}
		OutputManager.addToOutput(leds);
	}

}
