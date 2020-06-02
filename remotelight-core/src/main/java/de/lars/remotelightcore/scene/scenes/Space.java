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

import java.awt.Color;
import java.util.HashMap;
import java.util.Random;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.scene.Scene;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class Space extends Scene {
	
	private int pix;
	private HashMap<Integer, Integer> stars;  //ledNum, Brightness
	private Random r;

	public Space() {
		super("Space", 50);
	}
	
	@Override
	public void onEnable() {
		pix = RemoteLightCore.getLedNum();
		stars = new HashMap<>();
		r = new Random();
		OutputManager.addToOutput(PixelColorUtils.colorAllPixels(Color.BLACK, pix));
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		if(stars.size() < ((pix / 4) + r.nextInt(10))) {
			int led = r.nextInt(pix);
			int brightness = r.nextInt(200) + 56;
			
			stars.put(led, brightness);
		}
		
		for(int i = 0; i < pix; i++) {
			if(stars.containsKey(i)) {
				
				int b = stars.get(i);
				PixelColorUtils.setPixel(i, new Color(b, b , (b / 2)));
				
				b -= 5;
				if(b <= 0) {
					PixelColorUtils.setPixel(i, Color.BLACK);
					stars.remove(i);
				} else {
					stars.put(i, b);
				}
			}
		}
		super.onLoop();
	}

}
