package de.lars.remotelightcore.musicsync.modes;

import java.awt.Color;

import de.lars.remotelightcore.Main;
import de.lars.remotelightcore.musicsync.MusicEffect;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightcore.settings.types.SettingSelection.Model;
import de.lars.remotelightcore.utils.ArrayUtil;
import de.lars.remotelightcore.utils.color.ColorUtil;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class Energy extends MusicEffect {

	private SettingsManager s = Main.getInstance().getSettingsManager();
	private Color[] strip;
	private int binMin;
	private int binLowMax;
	private int binMidMax;
	private int binHighMax;
	
	public Energy() {
		super("Energy");
		String[] modes = new String[] {"RGB", "Mix", "Frequency", "Static"};
		s.addSetting(new SettingSelection("musicsync.energy.mode", "Mode", SettingCategory.MusicEffect, null, modes, "Static", Model.ComboBox));
		this.addOption("musicsync.energy.mode");
		s.addSetting(new SettingColor("musicsync.energy.color", "Color", SettingCategory.MusicEffect, "", Color.RED));
		this.addOption("musicsync.energy.color");
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
		String mode = ((SettingSelection) s.getSettingFromId("musicsync.energy.mode")).getSelected();
		float[] ampl = getSoundProcessor().getAmplitudes();
		double mul = 0.01 * this.getAdjustment() * Main.getLedNum() / 60; // multiplier for amount of pixels
		
		/* function: -a(x - ledNum/2)^ + 255 */
		
		if(mode.equals("RGB") || mode.equals("Mix")) {
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
			
			if(mode.equals("RGB")) {
				Color[] colors = {Color.RED, Color.GREEN, Color.BLUE};
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
				Color color = ((SettingColor) s.getSettingFromId("musicsync.energy.color")).getValue();
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
		
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, Main.getLedNum());
		int half = strip.length / 2;
		
		for(int aIndex = 0; aIndex < aArray.length; aIndex++) {
			double a = aArray[aIndex];
			Color c = colors[aIndex];
			
			for(int i = 0; i < strip.length; i++) {
				
				double brightness = -a * Math.pow(( i - half), 2) + 255;
				if(brightness < 0 || a == 0) continue;
				strip[i] = dim(c, brightness);
			}
		}
		
		OutputManager.addToOutput(strip);
	}
	
	private void showMix(double aR, double aG, double aB) {
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, Main.getLedNum());
		int half = strip.length / 2;
		
		for(int i = 0; i < strip.length; i++) {
			
			int red 	= (int) (-aR * Math.pow(( i - half), 2) + 255);
			int green 	= (int) (-aG * Math.pow(( i - half), 2) + 255);
			int blue 	= (int) (-aB * Math.pow(( i - half), 2) + 255);
			if(red < 0) 	red = 0;
			if(green < 0) 	green = 0;
			if(blue < 0)	blue = 0;
			strip[i] = new Color(red, green, blue);
		}
		
		OutputManager.addToOutput(strip);
	}
	
	
	private Color dim(Color c, double val) {
		int r = (int) (c.getRed() * val / 255.0);
		int g = (int) (c.getGreen() * val / 255.0);
		int b = (int) (c.getBlue() * val / 255.0);
		return new Color(r, g, b);
	}

}
