package de.lars.remotelightcore.utils.color.palette;

import java.util.Arrays;

import de.lars.remotelightcore.utils.color.Color;
import de.lars.remotelightcore.utils.color.ColorUtil;
import de.lars.remotelightcore.utils.maths.MathHelper;

public class EvenGradientPalette extends ColorPalette implements ColorGradient {
	
	public final static float DEFAULT_STEPSIZE = 0.1f;
	
	protected float stepSize;
	protected float currentStep;
	
	public EvenGradientPalette(Color... colors) {
		this(DEFAULT_STEPSIZE, colors);
	}
	
	public EvenGradientPalette(float stepSize, Color... colors) {
		this.stepSize = stepSize;
		add(colors);
	}
	
	/**
	 * Fade to the next color in the defined step size and get the color.
	 * Resets index to 0 when the last color is reached.
	 * @return		next color of the palette
	 */
	@Override
	public Color getNext() {
		if(listColor.size() == 0)
			throw new IllegalStateException("Could not return next item. The list is empty!");
		
		final float indexStepSize = 1.0f / (listColor.size() - 1);
		curIndex = findClosestIndex(currentStep, indexStepSize);
		int targetIndex = curIndex + 1;
		
		// step position for fading from current color to target color
		float regionStep = MathHelper.map(currentStep, curIndex*indexStepSize, targetIndex*indexStepSize, 0.0f, 1.0f);
		
		Color c = ColorUtil.fadeToColor(getColorAtIndex(curIndex), getColorAtIndex(targetIndex), regionStep);
		
		currentStep += stepSize; // increase step position by stepSize
		if(currentStep > 1.0f) {
			currentStep = 0.0f;
		}
		return c;
	}
	
	protected int findClosestIndex(float step, float stepSize) {
		int index = 0;
		for(int i = 0; i < listColor.size() - 1; i++) {
			if((i * stepSize) <= step) {
				index = i;
			} else {
				return index;
			}
		}
		return index;
	}
	
	/**
	 * Add the specified color(s) to the palette.
	 * @param colors	color(s) to add
	 * @return			the instance ({@code this})
	 */
	public EvenGradientPalette add(Color... colors) {
		if(colors != null)
			listColor.addAll(Arrays.asList(colors));
		return this;
	}
	
	/**
	 * Add the specified color to the palette.
	 * @param r			red
	 * @param g			green
	 * @param b			blue
	 * @return			the instance ({@code this})
	 */
	public EvenGradientPalette add(int r, int g, int b) {
		return add(new Color(r, g, b));
	}
	
	public float getStepSize() {
		return stepSize;
	}
	
	public void setStepSize(float stepSize) {
		this.stepSize = stepSize;
	}
	
	@Override
	public void resetStepPosition() {
		currentStep = 0.0f;
	}
	
	@Override
	public void skip(int indices) {
		super.skip(indices);
		final float indexStepSize = 1.0f / (listColor.size() - 1);
		currentStep = indexStepSize * curIndex;
	}
	
	public static EvenGradientPalette fromColorPalette(ColorPalette palette, float stepSize) {
		EvenGradientPalette gp = new EvenGradientPalette(stepSize);
		gp.listColor = palette.listColor;
		return gp;
	}

	/**
	 * Not supported by this palette type!
	 */
	@Override
	public void setReverseOnEnd(boolean reverse) {
	}

	/**
	 * Not supported by this palette type!
	 */
	@Override
	public boolean isReverseOnEnd() {
		return false;
	}
	
	/**
	 * Construct a default even gradient palette that fades from red to blue.
	 * @return		default even gradient palette
	 */
	public static EvenGradientPalette createDefault() {
		return new EvenGradientPalette(Color.RED, Color.BLUE);
	}

}
