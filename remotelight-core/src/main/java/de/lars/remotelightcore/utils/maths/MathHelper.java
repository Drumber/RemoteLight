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

package de.lars.remotelightcore.utils.maths;

import de.lars.remotelightcore.RemoteLightCore;

public class MathHelper {
	
	/*
	 * https://github.com/aykevl/micropython/blob/modpixel/extmod/modpixels.c
	 */
	public static int beat88(int beatsPerMinute88, int timebase) {
		long result = (((long) RemoteLightCore.getMillis() * beatsPerMinute88) >>> 16);
		return (int) result;
	}
	
	public static int beat16(short beatsPerMinute, int timebase) {
		if(Short.toUnsignedInt(beatsPerMinute) < 256) {
			beatsPerMinute = (short)(Short.toUnsignedInt(beatsPerMinute) << 8);
		}
		return (int)beat88(beatsPerMinute, timebase);
	}
	
	public static short beat8(short beatsPerMinute, int timebase) {
		return (short)(beat16(beatsPerMinute, timebase) >> 8);
	}
	
	
	public static int beatsin16(int bpm, int highest, int lowest, double stretch) {
		int range = highest - lowest;
		int rangeHalf = range / 2;
		int beat = beat16((short) bpm, 0);
		int result = (int) ((rangeHalf * Math.sin(beat * stretch)) + (range - rangeHalf));
		return result;
	}
	
	public static int beatsin8(int bpm, int highest, int lowest, double stretch) {
		int range = highest - lowest;
		int rangeHalf = range / 2;
		int beat = beat8((short) bpm, 0);
		int result = (int) ((rangeHalf * Math.sin(beat * stretch)) + (range - rangeHalf));
		return result;
	}
	
	
	/**
	 * Acceleration until halfway, then deceleration
	 * @param t current time
	 * @param b start value
	 * @param c change in value
	 * @param d duration
	 */
	public static float easeInOutQuad(float t,float b , float c, float d) {
		if ((t/=d/2) < 1) return c/2*t*t + b;
		return -c/2 * ((--t)*(t-2) - 1) + b;
	}
	
	/**
	 * Acceleration until halfway, then deceleration
	 * @param t value between 0.0 and 1.0
	 */
	public static float easeInOutQuadSimple(float t) {
		if(t < 0.5) return 2*t*t;
		return -1 + (4-2*t)*t;
	}
	
	/**
	 * Accelerating from zero velocity 
	 * @param t value between 0.0 and 1.0
	 */
	public static float easeInCubic(float t) {
		return t*t*t;
	}
	
	
	/**
	 * Map values / interpolate between two points
	 * @param a start/min value
	 * @param b end/max value
	 * @param f fraction/value
	 */
	public static float lerp(float a, float b, float f) {
		return a + f * (b - a);
	}
	
	
	/**
	 * Map value of range <code>from1 - to2</code> to range </code>from2 - to2</code>
	 * @param value Value between from1 and to2
	 * @param from1 minimum start value
	 * @param to1 maximum start value
	 * @param from2 minimum new value
	 * @param to2 maximum new value
	 * @return float between from2 and to2
	 */
	public static float map(float value, float from1, float to1, float from2, float to2) {
		return (value - from1) / (to1 - from1) * (to2 - from2) + from2;
	}
	
	/**
	 * Map value of range <code>from1 - to2</code> to range </code>from2 - to2</code>
	 * @param value Value between from1 and to2
	 * @param from1 minimum start value
	 * @param to1 maximum start value
	 * @param from2 minimum new value
	 * @param to2 maximum new value
	 * @return float between from2 and to2
	 */
	public static double map(double value, double from1, double to1, double from2, double to2) {
		return (value - from1) / (to1 - from1) * (to2 - from2) + from2;
	}
	
	public static float capMinMax(float value, float min, float max) {
		if(value > max)
			return max;
		if(value < min)
			return min;
		return value;
	}
	
	public static float percentageInRange(float value, float min, float max) {
		return ((value - min) * 100.0f) / (max - min);
	}
	
	public static int invertInRange(int value, int min, int max) {
		return (max + min) - value;
	}

}
