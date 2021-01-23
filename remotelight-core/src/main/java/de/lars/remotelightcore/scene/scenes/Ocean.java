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

import java.util.ArrayList;
import java.util.List;

import de.lars.remotelightcore.scene.Scene;
import de.lars.remotelightcore.utils.color.Color;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class Ocean extends Scene {
	
	private Color[] strip;
	private Color[] ocean;
	private boolean right;
	private int count, loops, pixels;

	public Ocean() {
		super("Ocean", 150);
	}
	
	@Override
	public void onEnable(int pixel) {
		count = 0; loops = 0; pixels = pixel;
		right = true;
		initOcean();
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, pixel);
		
		for(int i = 0; i < pixels; i++) {
			strip = PixelColorUtils.shiftPixelsRight(strip, 1);
			count++;
			if(count >= ocean.length)
				count = 0;
			
			strip[0] = ocean[count];
		}
		super.onEnable(pixel);
	}
	
	@Override
	public Color[] onEffect() {
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
			
			strip = PixelColorUtils.shiftPixelsRight(strip, 1);
			
			strip[0] = ocean[count];
			
		} else {
			
			strip = PixelColorUtils.shiftPixelsLeft(strip, 1);
			
			strip[pixels - 1] = ocean[count];
		}
		return strip;
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
