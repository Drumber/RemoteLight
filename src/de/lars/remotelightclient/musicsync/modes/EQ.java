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
package de.lars.remotelightclient.musicsync.modes;

import java.awt.Color;
import java.util.HashMap;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.musicsync.MusicEffect;
import de.lars.remotelightclient.musicsync.sound.SoundProcessing;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.utils.PixelColorUtils;

public class EQ extends MusicEffect {

	public EQ() {
		super("EQ");
	}
	
	@Override
	public void onLoop() {
		SoundProcessing soundProcessor = this.getSoundProcessor();
		int pix = Main.getLedNum();
		int half = pix / 2;
		int pixBand = half / 6; // pixels per frequency at each side(left/right) (6 bands)
		double mul = 0.318 * this.getAdjustment(); // multiplier for brightness
		HashMap<Integer, Color> pixelHash = new HashMap<>();
		int[] amp = soundProcessor.getSimpleAmplitudes();

		/*
		 *  half 1 (left)
		 */
		// low1
		int cl1 = (int) (amp[0] * mul); // cl1 = color low1
		if (cl1 > 255)
			cl1 = 255;
		for (int p = 0; p < pixBand; p++) {
			pixelHash.put(half - 1 - p, new Color(cl1, 0, 0));
		}
		// low2
		int cl2 = (int) (amp[1] * mul);
		if (cl2 > 255)
			cl2 = 255;
		for (int p = 0; p < pixBand; p++) {
			pixelHash.put(half - 1 - pixBand - p, new Color(0, cl2, 0));
		}
		// mid1
		int cm1 = (int) (amp[2] * mul);
		if (cm1 > 255)
			cm1 = 255;
		for (int p = 0; p < pixBand; p++) {
			pixelHash.put(half - 1 - 2 * pixBand - p, new Color(0, 0, cm1));
		}
		// mid2
		int cm2 = (int) (amp[3] * mul);
		if (cm2 > 255)
			cm2 = 255;
		for (int p = 0; p < pixBand; p++) {
			pixelHash.put(half - 1 - 3 * pixBand - p, new Color(0, cm2, 0));
		}
		// high1
		int ch1 = (int) (amp[4] * mul);
		if (ch1 > 255)
			ch1 = 255;
		for (int p = 0; p < pixBand; p++) {
			pixelHash.put(half - 1 - 4 * pixBand - p, new Color(ch1, 0, 0));
		}
		// mid2
		int ch2 = (int) (amp[5] * mul);
		if (ch2 > 255)
			ch2 = 255;
		for (int p = 0; p < pixBand; p++) {
			pixelHash.put(half - 1 - 5 * pixBand - p, new Color(0, 0, ch2));
		}

		
		/*
		 * half 2 (right)
		 */
		// low1
		for (int p = 0; p < pixBand; p++) {
			pixelHash.put(half + p, new Color(cl1, 0, 0));
		}
		// low2
		for (int p = 0; p < pixBand; p++) {
			pixelHash.put(half + pixBand + p, new Color(0, cl2, 0));
		}
		// mid1
		for (int p = 0; p < pixBand; p++) {
			pixelHash.put(half + 2 * pixBand + p, new Color(0, 0, cm1));
		}
		// mid2
		for (int p = 0; p < pixBand; p++) {
			pixelHash.put(half + 3 * pixBand + p, new Color(0, cm2, 0));
		}
		// high1
		for (int p = 0; p < pixBand; p++) {
			pixelHash.put(half + 4 * pixBand + p, new Color(ch1, 0, 0));
		}
		// high2
		for (int p = 0; p < pixBand; p++) {
			pixelHash.put(half + 5 * pixBand + p, new Color(0, 0, ch2));
		}

		OutputManager.addToOutput(PixelColorUtils.pixelHashToColorArray(pixelHash));
		
		super.onLoop();
	}

}
