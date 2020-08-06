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

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.musicsync.MusicEffect;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.utils.color.ColorUtil;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class Bump extends MusicEffect {
	
	private Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.YELLOW, Color.MAGENTA, Color.WHITE, Color.PINK};
	private int colorIndex = 0;
	private Color color = Color.black;
	private int pDelay; //previous delay

	public Bump() {
		super("Bump");
		
		this.addSetting(new SettingInt("musicsync.bump.speed", "Speed", SettingCategory.MusicEffect, "", 50, 20, 100, 5));
		this.addSetting(new SettingBoolean("musicsync.bump.background", "Background", SettingCategory.MusicEffect, "", true));
		this.addSetting(new SettingBoolean("musicsync.bump.center", "Center", SettingCategory.MusicEffect, "", false));
	}
	
	@Override
	public void onEnable() {
		pDelay = RemoteLightCore.getInstance().getMusicSyncManager().getDelay();
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		RemoteLightCore.getInstance().getMusicSyncManager().setDelay(pDelay);
		super.onDisable();
	}
	
	@Override
	public void onLoop() {
		RemoteLightCore.getInstance().getMusicSyncManager().setDelay(((SettingInt) getSetting("musicsync.bump.speed")).getValue());
		boolean background = ((SettingBoolean) getSetting("musicsync.bump.background")).getValue();
		boolean center = ((SettingBoolean) getSetting("musicsync.bump.center")).getValue();
		
		if(this.isBump()) {
			if(++colorIndex >= colors.length) {
				colorIndex = 0;
			}
			color = colors[colorIndex];	// bump -> new color
		} else {
			if(background) {
				color = ColorUtil.dimColor(colors[colorIndex], 5);
			} else {
				color = Color.BLACK;
			}
		}
		
		if(!center) {
			PixelColorUtils.shiftRight(3);
			for(int i = 0; i < 3; i++) {
				PixelColorUtils.setPixel(i, color);
			}
		} else {
			PixelColorUtils.shiftCenter(1);
			int ledNum = RemoteLightCore.getLedNum();
			PixelColorUtils.setPixel(ledNum/2, color);
			PixelColorUtils.setPixel(ledNum/2 - 1, color);
		}
		super.onLoop();
	}

}
