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

package de.lars.remotelightcore.utils.color;

import java.awt.Color;
import java.util.Random;

/*
 * adapted from https://stackoverflow.com/a/52498075
 */

public class RainbowWheel {
	/**
	 * Length of the rainbow array
	 */
	private final static int SIZE = 360;
	private static Color[] rainbow = new Color[SIZE];
	
	public static void init() {
		double jump = 1.0 / (SIZE * 1.0);
		for(int i = 0; i < SIZE; i++) {
			rainbow[i] = Color.getHSBColor((float) (jump * i), 1.0f, 1.0f);
		}
	}
	
	/**
	 * 
	 * @return color array from 0 to 360(excluded)
	 */
	public static Color[] getRainbow() {
		return rainbow;
	}

	/**
	 * 
	 * @return A random color from the rainbow array
	 */
	public static Color getRandomColor() {
		float random = new Random().nextFloat();
		return Color.getHSBColor(random, 1.0f, 1.0f);
	}
	
}
