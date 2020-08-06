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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.musicsync.MusicEffect;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.maths.TimeUtil;

public class Strobe extends MusicEffect {
	
	private Timer timer;
	private TimeUtil timeUtil;
	private boolean triggered;
	private int flashRatio = 4;
	private int flashDuration;
	private boolean flashSwitch;
	private Color color = Color.WHITE;

	public Strobe() {
		super("Strobe");
		this.addSetting(new SettingInt("musicsync.strobe.flashratio", "Flash ratio (per second)", SettingCategory.MusicEffect, "Flashes per second", 4, 0, 100, 1));
		this.addSetting(new SettingInt("musicsync.strobe.flashduration", "Flash duration (in ms)", SettingCategory.MusicEffect, "", 1500, 100, 50000, 100));
		this.addSetting(new SettingColor("musicsync.strobe.color", "Color", SettingCategory.MusicEffect, "", Color.WHITE));
		
		flashRatio = ((SettingInt) getSetting("musicsync.strobe.flashratio")).getValue();
		flashDuration = ((SettingInt) getSetting("musicsync.strobe.flashduration")).getValue();
		timer = new Timer(1000 / flashRatio, flashListener);
		timeUtil = new TimeUtil(flashDuration);
	}
	
	@Override
	public void onEnable() {
		triggered = false;
		timer.stop();
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		timer.stop();
		super.onDisable();
	}
	
	@Override
	public void onLoop() {
		color = ((SettingColor) getSetting("musicsync.strobe.color")).getValue();
		flashRatio = ((SettingInt) getSetting("musicsync.strobe.flashratio")).getValue();
		flashDuration = ((SettingInt) getSetting("musicsync.strobe.flashduration")).getValue();
		timer.setDelay(1000 / flashRatio);
		timeUtil.setInterval(flashDuration);
		
		if(triggered && timeUtil.hasReached()) {
			triggered = false;
			timer.stop();
			OutputManager.addToOutput(PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum()));
		}
		
		if(this.isBump() && !triggered) {
			flashSwitch = true;
			triggered = true;
			timer.start();
			timeUtil.reset();
		}
		
		super.onLoop();
	}
	
	private ActionListener flashListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(flashSwitch)
				OutputManager.addToOutput(PixelColorUtils.colorAllPixels(color, RemoteLightCore.getLedNum()));
			else
				OutputManager.addToOutput(PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum()));
			flashSwitch = !flashSwitch;
		}
	};

}
