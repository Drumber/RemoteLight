/*-
 * >===license-start
 * RemoteLight
 * ===
 * Copyright (C) 2019 - 2020 Lars O.
 * ===
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * <===license-end
 */

package de.lars.remotelightcore.musicsync.modes;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.musicsync.MusicEffect;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightcore.settings.types.SettingSelection.Model;
import de.lars.remotelightcore.utils.ArrayUtil;
import de.lars.remotelightcore.utils.color.Color;
import de.lars.remotelightcore.utils.color.ColorUtil;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class Energy extends MusicEffect {

	private Color[] strip;
	private int binMin;
	private int binLowMax;
	private int binMidMax;
	private int binHighMax;
	private String mode;
	
	private final String[] POS_MODES = {"Centered", "Left", "Right"};
	private String position = "centered";
	
	// settings
	private SettingColor sColorStatic;
	private SettingColor sColorLow;
	private SettingColor sColorMid;
	private SettingColor sColorHigh;
	
	public Energy() {
		super("Energy");
		// position
		this.addSetting(new SettingSelection("musicsync.energy.position", "Position Mode", SettingCategory.MusicEffect, "Position of the energy bar.", POS_MODES, POS_MODES[0], Model.ComboBox));
		// mode
		String[] modes = new String[] {"Stacked", "Mix", "Frequency", "Static"};
		this.addSetting(new SettingSelection("musicsync.energy.mode", "Mode", SettingCategory.MusicEffect, null, modes, "Static", Model.ComboBox));
		// static color
		sColorStatic = this.addSetting(new SettingColor("musicsync.energy.color.static", "Color", SettingCategory.MusicEffect, "", Color.RED));
		// stacked presets
		sColorLow = this.addSetting(new SettingColor("musicsync.energy.color.low", "Color Lows", SettingCategory.MusicEffect, "Color for low tones", Color.RED));
		sColorMid = this.addSetting(new SettingColor("musicsync.energy.color.mid", "Color Mids", SettingCategory.MusicEffect, "Color for mid tones", Color.GREEN));
		sColorHigh = this.addSetting(new SettingColor("musicsync.energy.color.high", "Color Highs", SettingCategory.MusicEffect, "Color for high tones", Color.BLUE));
	}
	
	@Override
	public void onEnable() {
		binMin = getSoundProcessor().hzToBin(40); 		// min bin index
		binLowMax = getSoundProcessor().hzToBin(150);	// max low bin index
		binMidMax = getSoundProcessor().hzToBin(3000);	// max middle bin index
		binHighMax = getSoundProcessor().hzToBin(12000);// max 12kHz
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		handleSettings();
		
		// prevent showing white dot in the center of the strip
		if(getMaxSpl() == 0.0) {
			// silent phase: show only black
			OutputManager.addToOutput(PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum()));
			return;
		}
		
		float[] ampl = getSoundProcessor().getAmplitudes();
		double mul = 0.01 * this.getAdjustment() * RemoteLightCore.getLedNum() / 60; // multiplier for amount of pixels
		
		/* function: -a(x)^2 + 255 */
		
		if(mode.equals("Stacked") || mode.equals("Mix")) {
			// amplitude to b of each rgb channel
			double avgLow = ArrayUtil.maxOfArray(ArrayUtil.subArray(ampl, binMin, binLowMax));		// red
			double avgMid = ArrayUtil.maxOfArray(ArrayUtil.subArray(ampl, binLowMax, binMidMax));	// green
			double avgHigh = ArrayUtil.maxOfArray(ArrayUtil.subArray(ampl, binMidMax, binHighMax));	// blue
			
			avgLow *= mul;
			avgMid *= mul;
			avgHigh *= mul;
			double[] aArray = {avgLow, avgMid, avgHigh};
			for(int i = 0; i < aArray.length; i++) {
				double a = 0;
				if(aArray[i] != 0) {
					a = 1.0 / aArray[i];
				}
				aArray[i] = a;
			}
			
			if(mode.equals("Stacked")) {
				Color[] colors = {sColorLow.get(), sColorMid.get(), sColorHigh.get()};
				show(aArray, colors);
			} else {
				showMix(aArray[0], aArray[1], aArray[2]);
			}
			
		} else {
			double avg = ArrayUtil.maxOfArray(ampl);
			avg *= mul;
			double a = 0;
			if(avg != 0) {
				a = 1.0 / avg;
			}
			
			if(mode.equals("Static")) {
				Color color = sColorStatic.get();
				show(a, color);
			} else {
				int start = getSoundProcessor().hzToBin(350);	// range from 350Hz...
				int end = getSoundProcessor().hzToBin(800);		// ... to 800Hz
				int binMax = ArrayUtil.maxIndexFromRangeOfArray(ampl, start, end);
				int hzMax = (int) getSoundProcessor().binToHz(binMax);
				Color color = ColorUtil.soundToColor(hzMax);
				show(a, color);
			}
		}
		
		super.onLoop();
	}
	
	
	private void show(double a, Color color) {
		show(new double[] {a}, new Color[] {color});
	}
	
	private void show(double[] aArray, Color[] colors) {
		if(aArray.length != colors.length)
			return;
		
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum());
		int center = getCenterPixel();
		
		for(int aIndex = 0; aIndex < aArray.length; aIndex++) {
			double a = aArray[aIndex];
			Color c = colors[aIndex];
			
			for(int i = 0; i <= center; i++) {
				
				double brightness = -a * Math.pow(i, 2) + 255;
				if(brightness < 0 || a == 0) continue;
				
				Color color = dim(c, brightness);
				setColorAtIndex(i, color, center);
			}
		}
		
		OutputManager.addToOutput(strip);
	}
	
	private void showMix(double aR, double aG, double aB) {
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum());
		int center = getCenterPixel();
		
		for(int i = 0; i <= center; i++) {
			
			int red 	= (int) (-aR * Math.pow(i, 2) + 255);
			int green 	= (int) (-aG * Math.pow(i, 2) + 255);
			int blue 	= (int) (-aB * Math.pow(i, 2) + 255);
			if(red < 0) 	red = 0;
			if(green < 0) 	green = 0;
			if(blue < 0)	blue = 0;
			setColorAtIndex(i, new Color(red, green, blue), center);
		}
		
		OutputManager.addToOutput(strip);
	}
	
	/**
	 * Set the color at the specified index.
	 * Handles the position mode.
	 * @param i			index of the pixel (0..center)
	 * @param color		color of the pixel
	 * @param center	the center value, or largest bar index
	 */
	private void setColorAtIndex(int i, Color color, int center) {
		switch(position.toLowerCase()) {
		case "centered":
			if(strip.length % 2 == 0) {
				strip[center - i] = color;
				strip[center + i + 1] = color;
			} else { // odd pixel number
				strip[center - i] = color;
				strip[center + i] = color;
			}
			break;
		case "right":
			strip[center - i] = color;
			break;
		case "left":
			strip[i] = color;
		}
	}
	
	/**
	 * Get the center pixel index or the index of the last pixel in
	 * Left/Right position mode.
	 * @return		largest bar index
	 */
	private int getCenterPixel() {
		if(!position.equalsIgnoreCase("centered"))
			return strip.length - 1; // return full strip size on Left/Right position mode
		
		float half = strip.length / 2.0f;
		boolean oddPixels = (strip.length % 2) != 0;
		int center = strip.length - (int) half - 1;
		if(oddPixels) {
			center = (int) half;
		}
		return center;
	}
	
	
	private Color dim(Color c, double val) {
		int r = (int) (c.getRed() * val / 255.0);
		int g = (int) (c.getGreen() * val / 255.0);
		int b = (int) (c.getBlue() * val / 255.0);
		return new Color(r, g, b);
	}
	
	private void handleSettings() {
		position = getSetting(SettingSelection.class, "musicsync.energy.position").get();
		String prevMode = mode;
		mode = ((SettingSelection) getSetting("musicsync.energy.mode")).getSelected();
		if(!mode.equals(prevMode)) { // mode changed
			// hide static color option
			this.hideSetting(sColorStatic, !mode.equals("Static"));
			// hide stacked preset color options
			boolean stacked = mode.equals("Stacked");
			this.hideSetting(sColorLow, !stacked);
			this.hideSetting(sColorMid, !stacked);
			this.hideSetting(sColorHigh, !stacked);
			// notify change event
			this.updateEffectOptions();
		}
	}

}
