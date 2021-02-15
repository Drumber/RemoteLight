package de.lars.remotelightcore.utils.color.palette;

import de.lars.remotelightcore.utils.color.Color;

public abstract class AbstractPalette {
	
	/**
	 * Clear the whole color palette
	 */
	public abstract void clear();
	
	public abstract Color getColorAtIndex(int index);
	
	public abstract void setColorAtIndex(int index, Color color);
	
	public abstract void addColor(Color color);
	
	/**
	 * Get the next color of the palette.
	 * @return			next color in palette
	 */
	public abstract Color getNext();
	
	/**
	 * Get the amount of colors in the palette.
	 * @return			color palette size
	 */
	public abstract int size();

}
