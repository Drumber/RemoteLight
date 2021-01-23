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

import java.util.HashMap;
import java.util.Random;

import de.lars.remotelightcore.scene.Scene;
import de.lars.remotelightcore.utils.color.Color;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class Jungle extends Scene {
	
	private Random r;
	private int pix, counter, lastGreenNum;
	private Color[] backgrnd;
	private HashMap<Integer, Color> points;
	private HashMap<Integer, Integer> pointsFade; //led, fade cycles
	private Color[] colors;

	public Jungle() {
		super("Jungle", 100);
	}
	
	@Override
	public void onEnable(int pixel) {
		r = new Random();
		this.pix = pixel;
		backgrnd = PixelColorUtils.colorAllPixels(Color.BLACK, pixel);
		Color[] greens = {new Color(0, 255, 0), new Color(150, 200, 0), new Color(110, 190, 5), new Color(0, 100, 5)};
		Color[] colors = {new Color(203, 255, 0), new Color(254, 102, 0), new Color(0, 50, 0), new Color(255, 255, 0)};
		this.colors = colors;
		points = new HashMap<Integer, Color>();
		pointsFade = new HashMap<>();
		/*
		 * SETUP
		 */
		counter = 0; lastGreenNum = 0;
		while(counter != (pix - 1)) {
			int amount = r.nextInt(pix / (pix / 15)) + 3;
			
			if((counter + amount) >= pix)
				amount = (pix - counter) - 1;
			
			int greenNum = 0;
			do {
				greenNum = r.nextInt(greens.length);
			} while(greenNum == lastGreenNum);
			lastGreenNum = greenNum;
			
			for(int i = counter; i <= (counter + amount); i++) {
				backgrnd[i] = greens[greenNum];
			}
			
			counter += amount;
		}
		super.onEnable(pixel);
	}
	
	@Override
	public Color[] onEffect() {
		Color[] strip = backgrnd.clone();
		
		if(points.size() < ((pix / 20) + r.nextInt(4))) {
			int led = r.nextInt(pix);
			Color c = colors[r.nextInt(colors.length)];
			
			points.put(led, c);
			pointsFade.put(led, 8);
		}
		
		for(int i = 0; i < pix; i++) {
			if(points.containsKey(i)) {
				//fade
				if(pointsFade.containsKey(i)) {
					Color c = points.get(i);
					int dim = pointsFade.get(i);
					
					int red = c.getRed() / dim;
					int green = c.getGreen() / dim;
					int blue = c.getBlue() / dim;
					
					strip[i] = new Color(red, green, blue);
					
					dim--;
					
					if(dim < 1) {
						pointsFade.remove(i);
					} else {
						pointsFade.put(i, dim);
					}
					
				} else {
					
					Color c = points.get(i);
					strip[i] = c;
					
					int red = c.getRed() - (c.getRed() / 6);
					int green = c.getGreen() - (c.getGreen() / 6);
					int blue = c.getBlue() - (c.getBlue() / 6);
					
					if(red < 10 && green < 10 && blue < 10) {
						Color bg = backgrnd[i];
						
						strip[i] = bg;
						points.remove(i);
						
					} else {
						points.put(i, new Color(red, green, blue));
					}
				}
			}
		}
		return strip;
	}

}
