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
import java.util.ArrayList;
import java.util.List;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.scene.Scene;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class Ocean extends Scene {
	
	private Color[] ocean;
	private boolean right;
	private int count, loops, pixels;

	public Ocean() {
		super("Ocean", 150);
	}
	
	@Override
	public void onEnable() {
		count = 0; loops = 0; pixels = RemoteLightCore.getLedNum();
		right = true;
		initOcean();
		
		for(int i = 0; i < pixels; i++) {
			PixelColorUtils.shiftRight(1);
			count++;
			if(count >= ocean.length)
				count = 0;
			
			PixelColorUtils.setPixel(0, ocean[count]);
		}
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		count++;
		if(count >= ocean.length) {
			count = 0;
			loops++;
			
			if(loops == 4) {
				if(right) right = false;
				else right = true;
			}
		}
		
		if(right) {
			
			PixelColorUtils.shiftRight(1);
			
			PixelColorUtils.setPixel(0, ocean[count]);
			
		} else {
			
			PixelColorUtils.shiftLeft(1);
			
			PixelColorUtils.setPixel(pixels - 1, ocean[count]);
		}
		super.onLoop();
	}
	
	
	private void initOcean() {
		List<Color> colors = new ArrayList<Color>();
		
		for(int i = 80; i < 256; i += 5)
			colors.add(new Color(0, 0, i));
		
		for(int i = 0; i < 256; i += 5)
			colors.add(new Color(0, i, 255));
		
		for(int i = 255; i > 80; i -= 5)
			colors.add(new Color(0, 255, i));
		
		for(int i = 255; i > 0; i -= 5)
			colors.add(new Color(0, i, 80));
		
		ocean = colors.toArray(new Color[colors.size()]);
	}

}
