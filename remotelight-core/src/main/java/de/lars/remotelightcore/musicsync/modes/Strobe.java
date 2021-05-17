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
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.utils.color.Color;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.maths.TimeUtil;

public class Strobe extends MusicEffect {
	
	private TimeUtil timeFlashes;
	private TimeUtil timeDuration;
	private boolean triggered;
	private int flashRatio = 4;
	private int flashDuration;
	private boolean flashSwitch;
	private Color color = Color.WHITE;

	public Strobe() {
		super("Strobe");
		flashRatio = this.addSetting(new SettingInt("musicsync.strobe.flashratio", "Flash ratio (per second)", SettingCategory.MusicEffect, "Flashes per second", 4, 1, 100, 1)).get();
		flashDuration = this.addSetting(new SettingInt("musicsync.strobe.flashduration", "Flash duration (in ms)", SettingCategory.MusicEffect, "", 1500, 100, 50000, 100)).get();
		this.addSetting(new SettingColor("musicsync.strobe.color", "Color", SettingCategory.MusicEffect, "", Color.WHITE));
		
		timeDuration = new TimeUtil(flashDuration, false);
		timeFlashes = new TimeUtil(1000 / flashRatio, true);
	}
	
	@Override
	public void onEnable() {
		triggered = false;
		flashSwitch = true;
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
	}
	
	@Override
	public void onLoop() {
		color = ((SettingColor) getSetting("musicsync.strobe.color")).get();
		flashRatio = ((SettingInt) getSetting("musicsync.strobe.flashratio")).get();
		flashDuration = ((SettingInt) getSetting("musicsync.strobe.flashduration")).get();
		timeFlashes.setInterval(1000 / flashRatio);
		timeDuration.setInterval(flashDuration);
		
		if(triggered && timeDuration.hasReached()) {
			triggered = false;
			OutputManager.addToOutput(PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum()));
		}
		
		if(this.isBump()) {
			flashSwitch = true;
			triggered = true;
			timeDuration.reset();
		}
		
		// FLASH
		if(triggered && (timeFlashes.hasReached() || this.isBump())) {
			if(flashSwitch)
				OutputManager.addToOutput(PixelColorUtils.colorAllPixels(color, RemoteLightCore.getLedNum()));
			else
				OutputManager.addToOutput(PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum()));
			flashSwitch = !flashSwitch;
		}
		
		super.onLoop();
	}

}
