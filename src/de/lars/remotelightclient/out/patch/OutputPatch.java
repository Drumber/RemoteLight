package de.lars.remotelightclient.out.patch;

import java.awt.Color;
import java.io.Serializable;

import de.lars.remotelightclient.devices.arduino.RgbOrder;
import de.lars.remotelightclient.utils.color.ColorUtil;
import de.lars.remotelightclient.utils.color.PixelColorUtils;

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


	public Color[] patchOutput(Color[] input) {
		input = shift(input);
		input = clone(input);
		return input;
	}
	
	public Color[] patchOutput(Color[] input, RgbOrder rgbOrder) {
		input = rgbOrder(input, rgbOrder);
		input = shift(input);
		input = clone(input);
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

}
