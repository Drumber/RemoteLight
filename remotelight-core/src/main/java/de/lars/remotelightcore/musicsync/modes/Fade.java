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
import de.lars.remotelightcore.musicsync.MusicSyncUtils;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class Fade extends MusicEffect {
	
	private Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.YELLOW, Color.MAGENTA, Color.WHITE, Color.PINK};
	private Color fadeLastColor = colors[0];
	private int color = 0;
	
	public Fade() {
		super("Fade");
	}
	
	@Override
	public void onLoop() {
		if(this.isBump()) {
			if(color < colors.length - 1) {
				color++;
			}
			else color = 0;
			fadeLastColor = colors[color];
		}
		OutputManager.addToOutput(PixelColorUtils.colorAllPixels(fadeLastColor, RemoteLightCore.getLedNum()));
		
		if((fadeLastColor.getRed() != 0) || (fadeLastColor.getGreen() != 0) || (fadeLastColor.getBlue() != 0)) fadeLastColor = MusicSyncUtils.dimColor(fadeLastColor, 2);
		else {
			if(color < colors.length - 1) {
				color++;
			}
			else color = 0;
			fadeLastColor = colors[color];
		}
		super.onLoop();
	}

}
