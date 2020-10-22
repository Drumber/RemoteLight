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

package de.lars.remotelightcore.scene.scenes;

import de.lars.remotelightcore.utils.color.Color;
import java.util.HashMap;
import java.util.Random;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.scene.Scene;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class Fire extends Scene {
	
	private int pixels;

	public Fire() {
		super("Fire", 100);
	}
	
	@Override
	public void onEnable() {
		pixels = RemoteLightCore.getLedNum();
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		HashMap<Integer, Color> pixelHash = new HashMap<>();
		int r = 255, g = 95, b = 12;
		
		for(int i = 0; i < pixels; i++) {
			
			int flicker = new Random().nextInt(40);
			int r1 = r - flicker;
			int g1 = g - flicker;
			int b1 = b - flicker;
			
			if(r1 < 0) r1 = 0;
			if(g1 < 0) g1 = 0;
			if(b1 < 0) b1 = 0;
			
			pixelHash.put(i, new Color(r1, g1, b1));
		}
		
		OutputManager.addToOutput(PixelColorUtils.pixelHashToColorArray(pixelHash));
		
		int delay = new Random().nextInt(100) + 50;
		super.setDelay(delay);
		
		super.onLoop();
	}

}
