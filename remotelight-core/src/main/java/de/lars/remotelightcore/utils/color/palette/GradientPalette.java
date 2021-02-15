package de.lars.remotelightcore.utils.color.palette;

import java.util.ArrayList;
import java.util.List;

import de.lars.remotelightcore.utils.color.Color;
import de.lars.remotelightcore.utils.color.ColorUtil;

public class GradientPalette extends AbstractPalette implements ColorGradient {
	
	protected List<Color> listColor;
	protected List<Float> listPosition;
	
	protected float stepSize;
	protected float currentStep;
	protected boolean reverseOnEnd;
	protected boolean directionReversed = false;
	
	public GradientPalette(float stepSize) {
		listColor = new ArrayList<Color>();
		listPosition = new ArrayList<Float>();
		this.stepSize = stepSize;
		if(stepSize < 0.0f || stepSize > 1.0f)
			throw new IllegalArgumentException("Step size must be between 0.0 and 1.0");
	}
	
	public GradientPalette() {
		this(0.005f);
	}
	
	@Override
	public void addColor(Color color) {
		float pos = 1.0f;
		while(listPosition.indexOf(pos) != -1) {
			pos -= Math.random() / 10;
			
			if(pos < 0.0f) {
				throw new IllegalStateException("Color could not be added because there were no empty position found.");
			}
		}
		
		int index = findIndexForPosition(pos);
		listColor.add(index, color);
		listPosition.add(index, pos);
	}
	
	@Override
	public void setColorAtIndex(int index, Color color) {
		listColor.set(index, color);
	}
	
	/**
	 * Add a color gradient to the palette.
	 * @param position		gradient position in range 0.0...1.0
	 * @param color			color for the specified position
	 * @return				the instance ({@code this})
	 */
	public GradientPalette add(float position, Color color) {
		if(position < 0.0f || position > 1.0f)
			throw new IllegalArgumentException("Position must be in range 0.0...1.0");
		if(listPosition.indexOf(position) != -1)
			throw new IllegalArgumentException("Position '" + position + "' does already exist.");
		int index = findIndexForPosition(position);
		listColor.add(index, color);
		listPosition.add(index, position);
		return this;
	}
	
	private int findIndexForPosition(float position) {
		int index = 0;
		for(int i = 0; i < listPosition.size(); i++) {
			if(listPosition.get(i) < position) {
				index = i + 1;
			}
		}
		return index;
	}
	
	/**
	 * Add a color gradient to the palette.
	 * @param position		gradient position in range 0.0...1.0
	 * @param r				red
	 * @param g				green
	 * @param b				blue
	 * @return				the instance ({@code this})
	 */
	public GradientPalette add(float position, int r, int g, int b) {
		return add(position, new Color(r, g, b));
	}
	
	/**
	 * Add a color gradient to the palette using a range from 0 to 255. The position
	 * will be mapped to a range between 0.0 and 1.0.
	 * @param position		gradient position in range 0...255
	 * @param color			color for the specified position
	 * @return				the instance ({@code this})
	 */
	public GradientPalette add(int position, Color color) {
		float pos = 1.0f / 255.0f * (float) position;
		return add(pos, color);
	}
	
	/**
	 * Add a color gradient to the palette using a range from 0 to 255. The position
	 * will be mapped to a range between 0.0 and 1.0.
	 * @param position		gradient position in range 0...255
	 * @param r				red
	 * @param g				green
	 * @param b				blue
	 * @return				the instance ({@code this})
	 */
	public GradientPalette add(int position, int r, int g, int b) {
		return add(position, new Color(r, g, b));
	}
	
	/**
	 * Remove a color gradient from the palette.
	 * @param position		gradient position
	 * @return				the instance ({@code this})
	 */
	public GradientPalette remove(float position) {
		int index = listPosition.indexOf(position);
		if(index > 0) {
			listColor.remove(index);
			listPosition.remove(index);
		}
		return this;
	}
	
	/**
	 * Remove the color at the specified index.
	 * @param index			index to be removed
	 * @return				the instance ({@code this})
	 */
	@Override
	public void removeColorAtIndex(int index) {
		listColor.remove(index);
		listPosition.remove(index);
	}
	
	@Override
	public Color getColorAtIndex(int index) {
		return listColor.get(index);
	}
	
	@Override
	public void clear() {
		listColor.clear();
		listPosition.clear();
	}

	/**
	 * Get the next color of the palette. Automatically resets when the last
	 * color is reached.
	 * @return				next color
	 */
	@Override
	public Color getNext() {
		if(listColor.size() == 0)
			throw new IllegalStateException("Could not return next item. The list is empty!");
		
		// get nearest start gradient position
		int startIndex = listPosition.size() - 1;
		for(; startIndex >= 0; startIndex--) {
			if(listPosition.get(startIndex) <= currentStep)
				break;
		}
		// get next gradient position
		int endIndex = startIndex + 1;
		if(endIndex >= listPosition.size())
			endIndex = 0;
		
		// offset start position
		float start = currentStep - listPosition.get(startIndex);
		if(start < 0)
			start += 1.0f;
		// offset end position
		float end = listPosition.get(endIndex) - listPosition.get(startIndex);
		if(end < 0)
			end += 1.0f;
		
		// calculate color fading step
		float step = start / end;
		Color c = ColorUtil.fadeToColor(listColor.get(startIndex), listColor.get(endIndex), step);
		
		
		if(reverseOnEnd && directionReversed) {
			currentStep -= stepSize;
		} else {
			currentStep += stepSize;
		}
		
		if(currentStep > 1.0f) {
			if(reverseOnEnd) {
				currentStep -= stepSize;
				directionReversed = !directionReversed;
			} else {
				currentStep -= 1.0f;
			}
		} else if(currentStep < 0.0f) {
			currentStep += stepSize;
			directionReversed = !directionReversed;
		}
		
		return c;
	}
	
	/**
	 * Get the list of the gradient positions.
	 * @return				copy of the gradient positions list
	 */
	public List<Float> getPositions() {
		return new ArrayList<Float>(listPosition);
	}
	
	/**
	 * Get the list of gradient colors.
	 * @return				copy of the gradient color list
	 */
	public List<Color> getColors() {
		return new ArrayList<Color>(listColor);
	}
	
	public float getStepSize() {
		return stepSize;
	}
	
	public void setStepSize(float stepSize) {
		this.stepSize = stepSize;
	}

	@Override
	public int size() {
		return listColor.size();
	}

	@Override
	public void setReverseOnEnd(boolean reverse) {
		this.reverseOnEnd = reverse;
	}

	@Override
	public boolean isReverseOnEnd() {
		return reverseOnEnd;
	}

}
