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

package de.lars.remotelightcore.out.patch;

import java.awt.Color;
import java.io.Serializable;

import de.lars.remotelightcore.devices.arduino.RgbOrder;
import de.lars.remotelightcore.utils.color.ColorUtil;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class OutputPatch implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4502618961495693998L;
	private int pixel;
	private int shift;		// shift all colors to left or right
	private int clone;		// number of times the strip is cloned
	private boolean cloneMirrored;

	
	public OutputPatch(int pixel) {
		this.pixel = pixel;
	}
	
	
	public void setPixelNumber(int pixel) {
		this.pixel = pixel;
	}
	
	
	public int getShift() {
		return shift;
	}


	public void setShift(int shift) {
		this.shift = shift;
	}


	public int getClone() {
		return clone;
	}


	public void setClone(int clone) {
		this.clone = clone;
	}
	
	
	public boolean isCloneMirrored() {
		return cloneMirrored;
	}
	
	
	public void setCloneMirrored(boolean cloneMirrored) {
		this.cloneMirrored = cloneMirrored;
	}
	
	
	public int getPatchedPixelNumber() {
		return (int) Math.round(pixel * 1.0D / (clone + 1));
	}
	
	public Color[] patchOutput(Color[] input, RgbOrder rgbOrder) {
		input = rgbOrder(input, rgbOrder);
		input = shift(input);
		input = clone(input);
		// mirror function if mirror is enabled but clone is 0
		if(clone <= 0 && cloneMirrored)
			input = mirror(input);
		return input;
	}
	
	
	private Color[] rgbOrder(Color[] input, RgbOrder rgbOrder) {
		Color[] out = new Color[input.length];
		for(int i = 0; i < out.length; i++) {
			out[i] = ColorUtil.matchRgbOrder(input[i], rgbOrder);
		}
		return out;
	}
	
	
	/**
	 * shift colors of strip x times
	 * @param input Color array
	 */
	private Color[] shift(Color[] input) {
		if(shift != 0 && Math.abs(shift) < input.length && input.length > 1) {
			Color[] tmp = new Color[input.length];
			
			int index;
			if(shift > 0)
				index = shift;
			else
				index = input.length + shift;
			
			for(int i = 0; i < input.length; i++) {
				tmp[index] = input[i];
				if(++index >= input.length) {
					index = 0;
				}
			}
			return tmp;
		}
		return input;
	}
	
	
	/**
	 * Clone / mirror the strip x times
	 * @param input Color array
	 */
	private Color[] clone(Color[] input) {
		if(clone != 0 && input.length >= getPatchedPixelNumber()) {
			
			Color[] tmp = PixelColorUtils.colorAllPixels(Color.BLACK, pixel);
			int counterClone = 0;	// counts the number of clones
			int indexInput = 0;
			byte summand = 1;
			
			for(int i = 0; i < tmp.length; i++) {
				tmp[i] = input[indexInput];
				
				indexInput += summand;		// +1 or -1 if mirrored
				
				if(indexInput >= input.length || indexInput < 0) {
					if(++counterClone <= clone) {
						if(cloneMirrored && summand == 1) {
							indexInput = input.length - 1;
							summand = -1;
						} else {
							indexInput = 0;
							summand = 1;
						}
						
					} else {
						break;
					}
				}
			}
			return tmp;
		}
		return input;
	}
	
	/**
	 * Mirror strip
	 * <p>use only if clone is not enabled
	 * @param input Color array
	 * @return mirrored color array
	 */
	private Color[] mirror(Color[] input) {
		if(input != null && input.length > 1) {
			Color[] tmp = PixelColorUtils.colorAllPixels(Color.BLACK, input.length);
			for(int i = input.length - 1; i >= 0; i--) {
				tmp[i] = input[input.length - 1 - i];
			}
			return tmp;
		}
		return input;
	}

}
