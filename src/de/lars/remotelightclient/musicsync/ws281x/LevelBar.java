package de.lars.remotelightclient.musicsync.ws281x;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import de.lars.remotelightclient.DataStorage;
import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.musicsync.MusicSync;
import de.lars.remotelightclient.musicsync.sound.SoundProcessing;
import de.lars.remotelightclient.network.Client;

public class LevelBar {
	
	private static Color background = Color.BLACK;
	public static Color color1 = (Color) DataStorage.getData(DataStorage.LEVELBAR_COLOR1);
	public static Color color2 = (Color) DataStorage.getData(DataStorage.LEVELBAR_COLOR2);
	public static Color color3 = (Color) DataStorage.getData(DataStorage.LEVELBAR_COLOR3);
	public static boolean autoChange = (boolean) DataStorage.getData(DataStorage.LEVELBAR_AUTOCHANGE);
	public static boolean smooth = (boolean) DataStorage.getData(DataStorage.LEVELBAR_SMOOTH);
	private static ArrayList<Color[]> pattern = new ArrayList<>();
	private static int count;
	private static int lastLeds = 0;
	private static int pix = Main.getLedNum();
	
	public static void levelBar(boolean bump, SoundProcessing soundProcessor) {
		int half = pix / 2;
		double mul = 0.1 * MusicSync.getSensitivity(); // multiplier for amount of pixels
		HashMap<Integer, Color> pixelHash = new HashMap<>();
		int[] amp = soundProcessor.getAmplitudes(); //6 bands
		int ampAv; //average of all amp bands
		int x = 0;
		for(int i = 0; i < amp.length; i++) {
			x += amp[i];
		}
		ampAv = x / amp.length; //average of all amp bands
		
		int leds = (int) (ampAv * mul); //how many leds should glow
		if(leds > half) leds = half;
		
		//Smooth
		if(smooth) {
			if(lastLeds > leds) {
				leds = lastLeds;
				lastLeds--;
			} else {
				lastLeds = leds;
			}
				
		}
		
		if(bump) {
			if(count < pattern.size() - 1)
				count++;
			else
				count = 0;
		}
		
		
		//half 1
		for(int i = 0; i < half; i++) {
			Color c = color1;
			
			if(autoChange) //auto color change
				c = pattern.get(count)[0];
			
			if((half - i) > leds) {
				c = background;
			} else {
				if(i < half / 3) {
					c = color3;
					
					if(autoChange) //auto color change
						c = pattern.get(count)[2];
				}
				else if(i < (half / 3) * 2) {
					c = color2;
					
					if(autoChange) //auto color change
						c = pattern.get(count)[1];
				}
			}
			pixelHash.put(i, c);
		}
		
		//half 2
		for(int i = 0; i < half; i++) {
			Color c = background;
			if(i < leds) {
				c = color1;
				
				if(autoChange) //auto color change
					c = pattern.get(count)[0];
				
					if(i >= (half / 3) * 2) {
						c = color3;
						
						if(autoChange) //auto color change
							c = pattern.get(count)[2];
					}
					else if(i >= half / 3) {
						c = color2;
						
						if(autoChange) //auto color change
							c = pattern.get(count)[1];
					}
			}
			pixelHash.put(i + half, c);
			
		}
		
		
		Client.sendWS281xList(pixelHash);
	}
	
	public static void initPattern() {
		pattern.add(new Color[] {Color.RED, new Color(255, 114, 0), new Color(255, 178, 0)});
		pattern.add(new Color[] {new Color(7, 0, 255), new Color(0, 146, 255), new Color(0, 255, 185)});
		pattern.add(new Color[] {new Color(100, 0, 255), new Color(212, 0, 255), new Color(255, 0, 154)});
		pattern.add(new Color[] {Color.RED, new Color(252, 197, 0), new Color(123, 255, 0)});
		pattern.add(new Color[] {Color.GREEN, new Color(185, 255, 0), new Color(255, 247, 0)});
		pattern.add(new Color[] {Color.GREEN, new Color(0, 255, 29), new Color(0, 255, 146)});
		pattern.add(new Color[] {new Color(255, 92, 0), new Color(0, 255, 46), new Color(254, 255, 0)});
		pattern.add(new Color[] {new Color(0, 51, 51), new Color(0, 0, 51), new Color(54, 61, 0)});
		pattern.add(new Color[] {new Color(51, 0, 0), new Color(0, 0, 51), new Color(0, 15, 0)});
		pattern.add(new Color[] {Color.RED, new Color(255, 10, 0), new Color(71, 18, 0)});
		
	}

}
