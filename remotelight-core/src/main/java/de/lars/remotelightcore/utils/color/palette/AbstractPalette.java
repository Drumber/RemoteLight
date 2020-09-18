package de.lars.remotelightcore.utils.color.palette;

import java.awt.Color;

public abstract class AbstractPalette {
	
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
