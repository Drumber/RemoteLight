/*-
 * >===license-start
 * RemoteLight
 * ===
 * Copyright (C) 2019 - 2020 Drumber
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

import java.awt.Color;
import java.util.Random;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.scene.Scene;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class SnowSparkle extends Scene {
	
	private Color[] strip;
	private int sparklesAmount;
	private int[] positions;

	public SnowSparkle() {
		super("SnowSparkle", 100);
	}
	
	@Override
	public void onEnable() {
		strip = PixelColorUtils.colorAllPixels(new Color(10, 10, 10), RemoteLightCore.getLedNum());
		sparklesAmount = strip.length / 60;
		if(sparklesAmount <= 0) sparklesAmount = 1;
		positions = new int[sparklesAmount];
		super.onEnable();
	}

	@Override
	public void onLoop() {
		
		if(positions[0] == -1) {
			// new sparkle
			for(int i = 0; i < sparklesAmount; i++) {
				positions[i] = new Random().nextInt(strip.length);
				strip[positions[i]] = new Color(150, 150, 200);
			}
			
			int delay = new Random().nextInt(40) + 60;
			super.setDelay(delay);
			
		} else {
			// clear sparkles
			for(int i = 0; i < sparklesAmount; i++) {
				strip[positions[i]] = new Color(10, 10, 10);
				positions[i] = -1;
			}
			
			int delay = new Random().nextInt(1000) + 100;
			super.setDelay(delay);
		}
		
		OutputManager.addToOutput(strip);
		super.onLoop();
	}
	
}
