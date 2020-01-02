package de.lars.remotelightclient.musicsync.modes;

import java.awt.Color;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.musicsync.MusicEffect;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;
import de.lars.remotelightclient.settings.types.SettingBoolean;
import de.lars.remotelightclient.settings.types.SettingColor;
import de.lars.remotelightclient.utils.ArrayUtil;
import de.lars.remotelightclient.utils.PixelColorUtils;

public class Energy extends MusicEffect {

	private SettingsManager s = Main.getInstance().getSettingsManager();
	private Color[] strip;
	private int binMin;
	private int binLowMax;
	private int binMidMax;
	private int binHighMax;
	
	public Energy() {
		super("Energy");
		s.addSetting(new SettingBoolean("musicsync.energy.rgbmode", "RGB Mode", SettingCategory.MusicEffect, "", true));
		this.addOption("musicsync.energy.rgbmode");
		s.addSetting(new SettingBoolean("musicsync.energy.staticcolor", "Static color", SettingCategory.MusicEffect, "", false));
		this.addOption("musicsync.energy.staticcolor");
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
		float[] ampl = getSoundProcessor().getAmplitudes();
		double mul = 0.01 * this.getAdjustment() * Main.getLedNum() / 60; // multiplier for amount of pixels
		
		/* function: -a(x - ledNum/2)² + 255 */
		
		if(!((SettingBoolean) s.getSettingFromId("musicsync.energy.staticcolor")).getValue()) {
			// amplitude to b of each rgb channel
			double avgLow = ArrayUtil.maxOfArray(ArrayUtil.subArray(ampl, binMin, binLowMax));		// red
			double avgMid = ArrayUtil.maxOfArray(ArrayUtil.subArray(ampl, binLowMax, binMidMax));	// green
			double avgHigh = ArrayUtil.maxOfArray(ArrayUtil.subArray(ampl, binMidMax, binHighMax));	// blue
			//System.out.println(avgLow + " | " + avgMid + " | " + avgHigh);
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
			
			if(!((SettingBoolean) s.getSettingFromId("musicsync.energy.rgbmode")).getValue()) {
				Color[] colors = {Color.RED, Color.GREEN, Color.BLUE};
				show(aArray, colors);
			} else {
				showRGB(aArray[0], aArray[1], aArray[2]);
			}
			
		} else {
			double avg = ArrayUtil.maxOfArray(ampl);
			avg *= mul;
			double a = 0;
			if(avg != 0) {
				a = 1.0 / avg;
			}
			Color color = ((SettingColor) s.getSettingFromId("musicsync.energy.color")).getValue();
			show(a, color);
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
	
	private void showRGB(double aR, double aG, double aB) {
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
