package de.lars.remotelightclient.arduino;

import java.awt.Color;
import java.util.HashMap;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.devices.arduino.GlediatorProtocol;

public class Arduino {
	
	private static Color[] strip = new Color[Main.getLedNum()];
	private static int dim = 1;
	
	
	public static void init() {
		for(int i = 0; i < strip.length; i++)
			strip[i] = Color.BLACK;
	}
	
	
	public static Color[] getStrip() {
		return strip;
	}
	
	public static Color getPixelColor(int pixel) {
		return strip[pixel];
	}
	
	public static void setColorAll(Color color) {
		Color c = dimColor(color);
		Color[] leds = new Color[Main.getLedNum()];
		for(int i = 0; i < Main.getLedNum(); i++) {
			leds[i] = c;
		}
		GlediatorProtocol.doOutput(leds);
		strip = leds;
	}
	
	public static void setColorAll(int r, int g, int b) {
		Color c = dimColor(new Color(r, g, b));
		Color[] leds = new Color[Main.getLedNum()];
		for(int i = 0; i < Main.getLedNum(); i++) {
			leds[i] = c;
		}
		GlediatorProtocol.doOutput(leds);
		strip = leds;
	}
	
	
	public static void setColorPixel(int pixel, int r, int g, int b) {		
		Color[] leds = new Color[Main.getLedNum()];
		for(int i = 0; i < Main.getLedNum(); i++) {
			if(i == pixel)
				leds[i] = dimColor(new Color(r, g, b));
			else
				leds[i] = strip[i];
		}
		GlediatorProtocol.doOutput(leds);
		strip = leds;
	}
	
	public static void setColorPixel(int pixel, Color color) {		
		Color[] leds = new Color[Main.getLedNum()];
		for(int i = 0; i < Main.getLedNum(); i++) {
			if(i == pixel)
				leds[i] = dimColor(color);
			else
				leds[i] = strip[i];
		}
		GlediatorProtocol.doOutput(leds);
		strip = leds;
	}
	
	
	public static void setFromPixelHash(HashMap<Integer, Color> pixelHash) {
		Color[] leds = new Color[Main.getLedNum()];
		for(int i = 0; i < Main.getLedNum(); i++) {
			if(pixelHash.get(i) == null)
				leds[i] = Color.BLACK;
			else
				leds[i] = dimColor(pixelHash.get(i));
		}
		GlediatorProtocol.doOutput(leds);
		strip = leds;
	}
	
	
	public static void shiftRight(int amount) {
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
		GlediatorProtocol.doOutput(leds);
		strip = leds;
	}
	
	public static void shiftLeft(int amount) {
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
		GlediatorProtocol.doOutput(leds);
		strip = leds;
	}
	
	
	public static void setDimValue(int dim) {
		Arduino.dim = dim;
	}
	
	public static Color dimColor(Color c) {
		try {
			int r = c.getRed() / dim, g = c.getGreen() / dim, b = c.getBlue() / dim;
			return new Color(r, g, b);
		} catch(Exception e) {
			
		}
		return c;
	}

}
