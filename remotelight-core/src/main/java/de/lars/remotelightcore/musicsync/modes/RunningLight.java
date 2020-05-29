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
package de.lars.remotelightcore.musicsync.modes;

import java.awt.Color;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.musicsync.MusicEffect;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class RunningLight extends MusicEffect {

	private int pDelay; //previous delay
	private SettingsManager s = RemoteLightCore.getInstance().getSettingsManager();
	private boolean centered;
	private Color[] strip;
	private double lastTime = 0;
	private final double multiplier = 0.2;
	private int groupSize;

	public RunningLight() {
		super("RunningLight");

		s.addSetting(new SettingBoolean("musicsync.runninglight.centered", "Centered", SettingCategory.MusicEffect, "", false));
		this.addOption("musicsync.runninglight.centered");
		s.addSetting(new SettingInt("musicsync.runninglight.groupsize", "Group size", SettingCategory.MusicEffect, "Number of LEDs per group", 3, 1, 5, 1));
		this.addOption("musicsync.runninglight.groupsize");
	}

	@Override
	public void onEnable() {
		strip = RemoteLightCore.getInstance().getOutputManager().getLastColors();
		pDelay = RemoteLightCore.getInstance().getMusicSyncManager().getDelay();
		RemoteLightCore.getInstance().getMusicSyncManager().setDelay(60);
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		RemoteLightCore.getInstance().getMusicSyncManager().setDelay(pDelay);
		super.onDisable();
	}

	@Override
	public void onLoop() {
		centered = ((SettingBoolean) s.getSettingFromId("musicsync.runninglight.centered")).getValue();
		groupSize = ((SettingInt) s .getSettingFromId("musicsync.runninglight.groupsize")).getValue();
		
		int half = RemoteLightCore.getLedNum() / 2;
		double pitch = this.getPitch();
		double time = this.getPitchTime();

		if (!centered) {
			PixelColorUtils.shiftRight(groupSize);
		}

		if (time != lastTime) {
			lastTime = time;

			for (int i = 0; i < groupSize; i++) {
				if (!centered) {
					strip[i] = getColor(pitch);

				} else {
					strip[half - 1 - i] = getColor(pitch); //right
					strip[half - i] = getColor(pitch); //left
				}
			}
		} else {
			for (int i = 0; i < groupSize; i++) {
				if (!centered) {
					strip[i] = Color.BLACK;

				} else {
					strip[half - 1 - i] = Color.BLACK;
					strip[half - i] = Color.BLACK;
				}
			}
		}
		
		if(centered) {
			this.center();
		}
			
		OutputManager.addToOutput(strip);
		super.onLoop();
	}

	private Color getColor(double pitch) {
		int value = (int) (multiplier * pitch);
		if (value >= RainbowWheel.getRainbow().length) {
			value = RainbowWheel.getRainbow().length - 1;
		}
		if (value < 0) {
			value = 0;
		}
		// show more red colors
		if (pitch < 60) {
			value /= 2;
		}
		return RainbowWheel.getRainbow()[value];
	}
	
	
	private void center() {
		int half = RemoteLightCore.getLedNum() / 2;
		
		for(int a = 0; a < groupSize; a++) {
			for(int i = 0; i < half - 1; i++) {
				strip[i] = strip[i + 1];
				strip[RemoteLightCore.getLedNum() - 1 - i] = strip[RemoteLightCore.getLedNum() - 2 - i];
			}
		}
	}

}
