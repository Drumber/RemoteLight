package de.lars.remotelightcore.utils.color.palette;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import de.lars.remotelightcore.utils.color.ColorUtil;

public class GradientPalette extends AbstractPalette {
	
	protected List<Color> listColor;
	protected List<Float> listPosition;
	
	protected float stepSize;
	protected float currentStep;
	protected int currentIndex;
	protected int targetIndex;
	
	public GradientPalette(float stepSize) {
		listColor = new ArrayList<Color>();
		listPosition = new ArrayList<Float>();
		this.stepSize = stepSize;
		if(stepSize < 0.0f || stepSize > 1.0f)
			throw new IllegalArgumentException("Step size must be between 0.0 and 1.0");
	}
	
	public GradientPalette() {
		this(0.1f);
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
		listColor.add(color);
		listPosition.add(position);
		return this;
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
			if(index == size()) {
				currentIndex = 0;
				increaseTargetIndex(1);
			}
		}
		return this;
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
		if(currentIndex == targetIndex && size() > 1)
			increaseTargetIndex(1);
		
		float diff = listPosition.get(targetIndex) - listPosition.get(currentIndex);
		if(diff < 0)
			diff += 1.0f;
		float step = currentStep / diff;
		Color c = ColorUtil.fadeToColor(listColor.get(currentIndex), listColor.get(targetIndex), step);
		
		step += stepSize;
		if(step > 1.0f)
			step -= 1.0f;
		
		currentIndex = targetIndex;
		increaseTargetIndex(1);
		return c;
	}
	
	protected void increaseTargetIndex(int amount) {
		targetIndex += amount;
		if(targetIndex >= size())
			targetIndex -= size();
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

	@Override
	public int size() {
		return listColor.size();
	}

}
