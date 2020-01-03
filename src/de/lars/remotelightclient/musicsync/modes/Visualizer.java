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
import java.util.Arrays;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.musicsync.MusicEffect;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;
import de.lars.remotelightclient.settings.types.SettingBoolean;
import de.lars.remotelightclient.settings.types.SettingColor;
import de.lars.remotelightclient.utils.ArrayUtil;
import de.lars.remotelightclient.utils.ColorUtil;
import de.lars.remotelightclient.utils.PixelColorUtils;
import de.lars.remotelightclient.utils.RainbowWheel;

public class Visualizer extends MusicEffect {
	
	private SettingsManager s = Main.getInstance().getSettingsManager();
	private Color color = Color.RED;
	private boolean rainbow = false;

	public Visualizer() {
		super("Visualizer");
		
		s.addSetting(new SettingColor("musicsync.visualizer.color", "Color", SettingCategory.MusicEffect, "", Color.RED));
		this.addOption("musicsync.visualizer.color");
		s.addSetting(new SettingBoolean("musicsync.visualizer.rainbow", "Rainbow", SettingCategory.MusicEffect, "", false));
		this.addOption("musicsync.visualizer.rainbow");
	}
	
	@Override
	public void onLoop() {
		this.initOptions();
		
		float[] ampl = getSoundProcessor().getAmplitudes(); //amplitudes
		int bin12khz = getSoundProcessor().hzToBin(6000); //get binIndex of 12kHz
		int bin50khz = getSoundProcessor().hzToBin(50); //get binIndex of 50Hz
		if(ampl.length > bin12khz) {
			ampl = Arrays.copyOfRange(ampl, bin50khz, bin12khz); //we only want to show frequencies from 50Hz up to 12khz
		}
		
		int frequncLed = ampl.length / Main.getLedNum(); //how many frequencies does a led show (frequency range per led)
		
		for(int i = 0; i < Main.getLedNum(); i++) {
			double ledAmpl = ArrayUtil.maxOfArray(Arrays.copyOfRange(ampl, frequncLed*i, frequncLed*(i + 1)));	// max amplitude of the LEDs range
			int brightness = amplitudeToBrightness(ledAmpl);
			
			Color c = ColorUtil.dimColor(getColor(ledAmpl, i), brightness);
			
			PixelColorUtils.setPixel(i, c);
		}
		
		super.onLoop();
	}
	
	private int amplitudeToBrightness(double ampl) {
		if(ampl < 3.0) {
			return 0;
		}
		return (int) (ampl * getAdjustment());
	}
	
	private Color getColor(double ampl, int led) {
		if(rainbow) {
			int mltiplr = RainbowWheel.getRainbow().length / Main.getLedNum();
			return RainbowWheel.getRainbow()[led * mltiplr];
		} else {
			return color;
		}
	}
	
	private void initOptions() {
		color = ((SettingColor) s.getSettingFromId("musicsync.visualizer.color")).getValue();
		rainbow = ((SettingBoolean) s.getSettingFromId("musicsync.visualizer.rainbow")).getValue();
	}

}
