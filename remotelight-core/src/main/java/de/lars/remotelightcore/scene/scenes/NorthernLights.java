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
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import de.lars.remotelightcore.scene.Scene;
import de.lars.remotelightcore.utils.color.Color;

public class NorthernLights extends Scene {
	
	private Color[] colors;
	private int pix;
	private int count;
	private Color[] strip;
	private Random r;
	private boolean newColor;
	private HashMap<Integer, NorthernLightTrail> lights;

	public NorthernLights() {
		super("NorthernLight", 100);
	}
	
	@Override
	public void onEnable(int pixel) {
		Color[] colors = {new Color(0, 207, 82), new Color(3, 46, 62), new Color(25, 100, 106), new Color(0, 198, 144),
				new Color(0, 223, 150), new Color(142, 0, 251)};
		this.colors = colors;
		this.pix = pixel;
		count = 0;
		
		strip = new Color[pix];
		strip = this.initStrip(strip);
		
		r = new Random();
		newColor = true;
		lights = new HashMap<>();
		
		super.onEnable(pixel);
	}
	
	@Override
	public Color[] onEffect() {
		count = lights.size();
		
		if(count <= (pix / 20))
			newColor = true;
		else
			newColor = false;
		
		if(newColor) {
			int ranColor = r.nextInt(colors.length);
			int startPix = r.nextInt(pix);
			int direction = r.nextInt(2);
			
			boolean right;
			
			if(direction == 0) right = true;
			else right = false;
			
			
			lights.put(startPix, new NorthernLightTrail(startPix, colors[ranColor], right));
		}
		
		//dim all
		for(int i = 0; i < pix; i++) {
			int red = strip[i].getRed() - (strip[i].getRed() / 8);
			int green = strip[i].getGreen() - (strip[i].getGreen() / 8);
			int blue = strip[i].getBlue() - (strip[i].getBlue() / 8);
			
			if(red < 10 && green < 10 && blue < 10)
				strip[i] = Color.BLACK;
			else
				strip[i] = new Color(red, green, blue);
		}
		
		//set pixel color
		List<Integer> alreadyPut = new ArrayList<>();
		
		for(int i = 0; i < pix; i++) {
			
			if(lights.containsKey(i) && !alreadyPut.contains(i)) {
				
				NorthernLightTrail led = lights.get(i);
				
				strip[i] = led.color;
				
				if(led.right) {
					
					if(i != (pix - 1)) {
						lights.put((i + 1), led);
						alreadyPut.add((i + 1));
						
					}
					
					lights.remove(i);
					
				} else {
					
					if(i != 0) {
						lights.put((i - 1), led);
						alreadyPut.add((i - 1));
						
					}
					
					lights.remove(i);
				}
			}
		}
		
		return strip;
	}
	
	
	private Color[] initStrip(Color[] strip) {
		for(int i = 0; i < strip.length; i++)
			strip[i] = Color.BLACK;
		return strip;
	}

}
