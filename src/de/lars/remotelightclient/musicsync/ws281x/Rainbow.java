package de.lars.remotelightclient.musicsync.ws281x;

import java.awt.Color;
import java.util.HashMap;

import de.lars.remotelightclient.DataStorage;
import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.arduino.RainbowWheel;
import de.lars.remotelightclient.musicsync.MusicSync;
import de.lars.remotelightclient.musicsync.tarosdsp.PitchDetector;
import de.lars.remotelightclient.network.Client;

public class Rainbow {
	
	private static int pix = Main.getLedNum();
	private static int half = pix / 2;
	private static int lastLeds = 0;
	private static Color[] strip;
	private static int step = 0;
	
	public static boolean smothRise = true;
	public static boolean smothFall = true;
	public static int steps = 5;
	
	public static void rainbow(boolean bump) {
		HashMap<Integer, Color> pixelHash = new HashMap<>();
		
		double mul = 0.1 * MusicSync.getSensitivity(); // multiplier for amount of pixels
		int[] amp = PitchDetector.getAmplitudes(); //6 bands
		
		int x = 0;
		for(int i = 0; i < amp.length; i++) {
			x += amp[i];
		}
		int ampAv = x / amp.length; //average of all amp bands
		
		int leds = (int) (ampAv * mul); //how many leds should glow
		if(leds > half) leds = half;
		
		//Smooth
		if(smothRise && smothFall) {
			if(lastLeds > leds) {
				leds = lastLeds;
				lastLeds--;
			} else {
				lastLeds += 2;
				if(lastLeds > leds) lastLeds = leds;
				leds = lastLeds;
			}
			
		} else if(smothRise) {
			if(lastLeds > leds) {
				lastLeds = leds;
			} else {
				lastLeds += 2;
				if(lastLeds > leds) lastLeds = leds;
				leds = lastLeds;
			}
			
		} else if(smothFall) {
			if(lastLeds > leds) {
				leds = lastLeds;
				lastLeds--;
			} else {
				lastLeds = leds;
			}
			
		}
		
		
		//Rainbow
		for(int i = 0; i < pix; i++) {
			pixelHash.put(i, strip[i]);
		}
		
		if(bump)
			step += steps + (steps / 2) + 20;
		else
			step += steps;

		if(step >= RainbowWheel.getRainbow().length)
			step = 0;
		
		//move
		for(int i = 0; i < half; i++) {
			Color c = RainbowWheel.getRainbow()[step];
			
			if(i == (half - 1)) {
				strip[i] = c;	//half1
				strip[half] = c;//half2
				
			} else {
				strip[i] = strip[i + 1];
				strip[pix - 1 - i] = strip[pix - 2 - i];
			}
		}
		
		//effect
		for(int i = 0; i < (half - leds); i++) {
			pixelHash.put(i, Color.BLACK);
			pixelHash.put(pix - 1 - i, Color.BLACK);
		}
		
		Client.sendWS281xList(pixelHash);
		
	}
	
	
	public static void init() {
		strip = new Color[pix];
		
		for(int i = 0; i < half; i++) {
			step += 5;
			if(step >= RainbowWheel.getRainbow().length)
				step = 0;
			
			Color c = RainbowWheel.getRainbow()[step];
			//half1
			strip[(half - 1 - i)] = c;
			//half2
			strip[(half + i)] = c;
		}
		
		if(DataStorage.isStored(DataStorage.RAINBOW_SMOOTH_RISE)) {
			smothRise = (boolean) DataStorage.getData(DataStorage.RAINBOW_SMOOTH_RISE);
			smothFall = (boolean) DataStorage.getData(DataStorage.RAINBOW_SMOOTH_FALL);
			steps = (int) DataStorage.getData(DataStorage.RAINBOW_STEPS);
		}
	}

}
