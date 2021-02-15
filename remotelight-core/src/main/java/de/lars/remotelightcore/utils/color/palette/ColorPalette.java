package de.lars.remotelightcore.utils.color.palette;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import de.lars.remotelightcore.utils.color.Color;

public class ColorPalette extends AbstractPalette implements Iterable<Color> {
	
	protected List<Color> listColor;
	protected int curIndex = 0;
	
	public ColorPalette(Color... colors) {
		listColor = new ArrayList<Color>();
		add(colors);
	}
	
	/**
	 * Get the color at the specified index.
	 * @param index		index of the color
	 * @return			the color at the specified index in the list
	 */
	@Override
	public Color getColorAtIndex(int index) {
		return listColor.get(index);
	}
	
	/**
	 * Set the color at the specified index.
	 * @param index		index of the color
	 * @param color		the color that should be set at the index
	 */
	@Override
	public void setColorAtIndex(int index, Color color) {
		listColor.set(index, color);
	}
	
	/**
	 * Get the next color of this palette. Resets index to {@code 0} when the
	 * last color is reached.
	 * @return			next color of the palette
	 * @throws IllegalStateException
	 * 					if the color palette is empty
	 */
	@Override
	public Color getNext() {
		if(listColor.size() == 0)
			throw new IllegalStateException("Could not return next item. The list is empty!");
		Color item = listColor.get(curIndex);
		if(++curIndex >= listColor.size())
			curIndex = 0;
		return item;
	}
	
	/**
	 * Get a random color from the palette.
	 * @return			randomly picked color
	 */
	public Color getRandom() {
		if(listColor.size() == 0)
			throw new IllegalStateException("Could not return next item. The list is empty!");
		int index = new Random().nextInt(size());
		return getColorAtIndex(index);
	}
	
	/**
	 * Skip the specified amount of indices.
	 * @param indices	amount of indices to skip
	 * @throws IllegalStateException
	 * 					if the color palette is empty
	 */
	public void skip(int indices) {
		if(listColor.size() == 0)
			throw new IllegalStateException("Could not return next item. The list is empty!");
		curIndex += indices;
		if(curIndex >= listColor.size())
			curIndex -= listColor.size();
	}
	
	public int size() {
		return listColor.size();
	}
	
	@Override
	public void addColor(Color color) {
		this.add(color);
	}
	
	/**
	 * Add the specified color at the given index.
	 * @param index		index at which the color is added
	 * @param color		color to add
	 * @return			the instance ({@code this})
	 */
	public ColorPalette add(int index, Color color) {
		listColor.add(index, color);
		return this;
	}
	
	/**
	 * Add the specified color(s) to the palette.
	 * @param colors	color(s) to add
	 * @return			the instance ({@code this})
	 */
	public ColorPalette add(Color... colors) {
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
	public ColorPalette add(int r, int g, int b) {
		return add(new Color(r, g, b));
	}
	
	/**
	 * Remove a color from the palette.
	 * @param color		color to remove
	 */
	public void remove(Color color) {
		listColor.remove(color);
	}
	
	/**
	 * Remove the color at the specified index.
	 * @param index		index of the color
	 */
	public void remove(int index) {
		listColor.remove(index);
	}
	
	@Override
	public void clear() {
		listColor.clear();
	}
	
	/**
	 * Get the {@link ArrayList} used by this color palette.
	 * @return			array list instance
	 */
	public List<Color> getList() {
		return listColor;
	}

	@Override
	public Iterator<Color> iterator() {
		Iterator<Color> it = new Iterator<Color>() {
			
			private int curIndex = 0;

			@Override
			public boolean hasNext() {
				return curIndex < listColor.size();
			}

			@Override
			public Color next() {
				return listColor.get(curIndex++);
			}
			
			@Override
			public void remove() {
				if(curIndex - 1 < 0)
					throw new IllegalStateException("Nothing to remove");
				listColor.remove(--curIndex);
			}
			
		};
		return it;
	}

}
