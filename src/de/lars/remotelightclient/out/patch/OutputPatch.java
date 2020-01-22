package de.lars.remotelightclient.out.patch;

import java.awt.Color;
import java.io.Serializable;

public class OutputPatch implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4502618961495693998L;
	private int shift;		// shift all colors to left or right
	private int clone;		// number of time the strip is cloned


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


	public Color[] patchOutput(Color[] input) {
		input = shift(input);
		clone(input);
		return input;
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
	private void clone(Color[] input) {
		//TODO
	}

}
