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

package de.lars.remotelightcore.utils;

import java.util.Arrays;

public class ArrayUtil {

	public static int sumOfArray(int[] array) {
		int sum = 0;
		for(int v : array)
			sum += v;
		return sum;
	}
	
	public static float sumOfArray(float[] array) {
		float sum = 0F;
		for(float v : array)
			sum += v;
		return sum;
	}
	
	
	public static int avgOfArray(int[] array) {
		int sum = sumOfArray(array);
		return sum / array.length;
	}
	
	public static float avgOfArray(float[] array) {
		float sum = sumOfArray(array);
		return sum / array.length;
	}
	
	
	public static float maxOfArray(float[] array) {
		float max = 0;
		for(float v : array) {
			if(v > max)
				max = v;
		}
		return max;
	}
	
	public static double maxOfArray(double[] array) {
		double max = 0;
		for(double v : array) {
			if(v > max)
				max = v;
		}
		return max;
	}
	
	
	public static int maxIndexOfArray(float[] array) {
		int index = 0;
		float max = 0;
		for(int i = 0; i < array.length; i++) {
			if(array[i] > max) {
				max = array[i];
				index = i;
			}
		}
		return index;
	}
	
	public static int maxIndexFromRangeOfArray(float[] array, int start, int end) {
		int index = 0;
		float max = 0;
		for(int i = start; i < end; i++) {
			if(array[i] > max) {
				max = array[i];
				index = i;
			}
		}
		return index;
	}
	
	
	/**
	 * Get subarray of an array
	 * @param ampl Original array
	 * @param startIndex From index (inclusive)
	 * @param endIndex To index (inclusive)
	 * @return Subarray
	 */
	public static float[] subArray(float[] ampl, int startIndex, int endIndex) {
		return Arrays.copyOfRange(ampl, startIndex, endIndex + 1);
	}
	
}
