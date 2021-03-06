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

public class Sunset extends Scene {
	
	private Color[] strip;
	private Color[] sun;
	private int count;

	public Sunset() {
		super("Sunset", 150);
	}
	
	@Override
	public void onEnable(int pixel) {
		count = 0;
		initSun();
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, pixel);
		
		for(int i = 0; i < strip.length; i++) {
			strip = PixelColorUtils.shiftPixelsRight(strip, 1);
			count++;
			if(count >= sun.length)
				count = 0;
			
			strip[0] = sun[count];
		}
		super.onEnable(pixel);
	}
	
	@Override
	public Color[] onEffect() {
		strip = PixelColorUtils.shiftPixelsRight(strip, 1);
		
		count++;
		if(count >= sun.length)
			count = 0;
		
		strip[0] = sun[count];
		return strip;
	}
	
	
	private void initSun() {
		List<Color> colors = new ArrayList<Color>();
		
		for(int i = 0; i < 106; i += 5)
			colors.add(new Color(150 + i, 0, 0));
		
		colors.add(new Color(255, 0, 0));
		for(int i = 0; i < 160; i++)
			colors.add(new Color(255, i, 0));
	
		for(int i = 160; i > 0; i--)
			colors.add(new Color(255, i, 0));
		colors.add(new Color(255, 0, 0));
		
		for(int i = 0; i < 106; i += 5)
			colors.add(new Color(255 - i, 0, 0));
		
		sun = colors.toArray(new Color[colors.size()]);
	}

}
