package de.lars.remotelightcore.utils.color.palette;

public interface ColorGradient {
	
	/**
	 * Reverse color palette when last color was reached
	 * @param reverse		true if palette should be reversed on end
	 */
	public void setReverseOnEnd(boolean reverse);
	public boolean isReverseOnEnd();
	
	public void setStepSize(float stepSize);
	public float getStepSize();
	
	public void resetStepPosition();

}
