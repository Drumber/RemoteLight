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
package de.lars.remotelightcore.musicsync;

import java.awt.Color;

import de.lars.remotelightcore.musicsync.sound.SoundProcessing;

public class MusicSyncUtils {
	
	private boolean bump = false;
	private double volume, lastVolume, maxVolume = 2.0,
						spl, maxSpl, minSpl, lastMaxSpl, lastMinSpl, avgBump;
	private int noInfoCounter, sameMinSplCounter, sameMaxSplCounter;
	
	public MusicSyncUtils() {
		
	}
	
	public void process(float pitch, double rms, double pitchTime, double sensitivity, SoundProcessing soundProcessor) {
		this.volume = rms;
		
		spl = soundProcessor.getCurrentSPL();
		//increase minSpl / decrease maxSpl a little bit if the song has only a short quiet/loud part
		if(lastMinSpl == minSpl) {
			if(sameMinSplCounter <= 800) sameMinSplCounter++;
			else
				if((minSpl + 1) < maxSpl) minSpl = minSpl + 1;
		} else
			sameMinSplCounter = 0;
		if(lastMaxSpl == maxSpl) {
			if(sameMaxSplCounter <= 1400) sameMaxSplCounter++;
			else
				if((maxSpl - 1) > minSpl) maxSpl = maxSpl - 1;
		} else
			sameMaxSplCounter = 0;
		
		if(minSpl == 0) {
			minSpl = spl;
			sameMinSplCounter = 0;
		}
		if(spl < minSpl) {
			minSpl = spl;
			sameMinSplCounter = 0;
		}
		if(spl > maxSpl) {
			maxSpl = spl;
			sameMaxSplCounter = 0;
		}
		
		lastMinSpl = minSpl;
		lastMaxSpl = maxSpl;
		
		if(volume > maxVolume) maxVolume = volume;
		if(volume < 0.02) volume = 0; //no sound = silent
		
		if(volume - lastVolume > sensitivity * 2) avgBump = (avgBump + (volume - lastVolume)) / 2.0; //if there is a big change in volume
		bump = (volume - lastVolume > avgBump * .9); //trigger a bump
		
		lastVolume = volume;
		
		/*
		 * no sound info detection
		 */
		if(spl == 0) {
			if(noInfoCounter <= 15) noInfoCounter++;
			else { //time is over
				spl = 0;
				maxSpl = 0;
			}
		} else {
			noInfoCounter = 0;
		}
	}
	
	public boolean isBump() {
		return bump;
	}
	
	public double getVolume() {
		return volume;
	}
	
	public double getMaxSpl() {
		return maxSpl;
	}
	
	public double getMinSpl() {
		return minSpl;
	}
	
	public double getSpl() {
		return spl;
	}
	
	public static Color dimColor(Color color, int dimValue) {
		int r = color.getRed() - dimValue;
		int g = color.getGreen() - dimValue;
		int b = color.getBlue() - dimValue;
		if(r < 0) r = 0;
		if(g < 0) g = 0;
		if(b < 0) b = 0;
		return new Color(r, g, b);
	}
	
	public static Color changeBrightness(Color color, int brightness) {
		int diff = (255 - brightness);
		int r = color.getRed() - diff;
		int g = color.getGreen() - diff;
		int b = color.getBlue() - diff;
		//System.out.println(r + " " + g + " " + b);
		if(r < 0) r = 0;
		if(g < 0) g = 0;
		if(b < 0) b = 0;
		if(r > 255) r = 255;
		if(g > 255) g = 255;
		if(b > 255) b = 255;
		
		return new Color(r, g, b);
	}

}
