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

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.musicsync.MusicEffect;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;
import de.lars.remotelightclient.settings.types.SettingBoolean;
import de.lars.remotelightclient.settings.types.SettingColor;
import de.lars.remotelightclient.utils.color.ColorUtil;
import de.lars.remotelightclient.utils.color.RainbowWheel;

public class Visualizer extends MusicEffect {
	
	private SettingsManager s = Main.getInstance().getSettingsManager();
	private Color[] strip;
	private boolean rainbow = false;

	public Visualizer() {
		super("Visualizer");
		
		s.addSetting(new SettingColor("musicsync.visualizer.color", "Color", SettingCategory.MusicEffect, "", Color.RED));
		this.addOption("musicsync.visualizer.color");
		s.addSetting(new SettingBoolean("musicsync.visualizer.rainbow", "Rainbow", SettingCategory.MusicEffect, "", false));
		this.addOption("musicsync.visualizer.rainbow");
	}
	
	@Override
	public void onEnable() {
		strip = new Color[Main.getLedNum()];
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		rainbow = ((SettingBoolean) s.getSettingFromId("musicsync.visualizer.rainbow")).getValue();
		
		float[] ampl = getSoundProcessor().getAmplitudes(); //amplitudes
		int[] fftData = getSoundProcessor().computeFFT(ampl, strip.length, getAdjustment());
		
		for(int i = 0; i < Main.getLedNum(); i++) {
			int brightness = fftData[i];
			
			Color c = ColorUtil.dimColor(getColor(i), brightness);
			strip[i] = c;
		}
		
		OutputManager.addToOutput(strip);
		super.onLoop();
	}
	
	private Color getColor(int led) {
		if(rainbow) {
			int mltiplr = RainbowWheel.getRainbow().length / Main.getLedNum();
			return RainbowWheel.getRainbow()[led * mltiplr];
		} else {
			return ((SettingColor) s.getSettingFromId("musicsync.visualizer.color")).getValue();
		}
	}

}
