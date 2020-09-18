package de.lars.remotelightcore.utils.color.palette;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ColorPalette implements Iterable<Color> {
	
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
	public Color get(int index) {
		return listColor.get(index);
	}
	
	/**
	 * Get the next color of this palette. Resets index to {@code 0} when the
	 * last color is reached.
	 * @return			next color of the palette
	 * @throws IllegalStateException
	 * 					if the color palette is empty
	 */
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
		return get(index);
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
	
	/**
	 * Get the amount of colors of the palette.
	 * @return			color palette size
	 */
	public int size() {
		return listColor.size();
	}
	
	/**
	 * Add the specified color(s) to the palette.
	 * @param colors	color(s) to add
	 */
	public void add(Color... colors) {
		if(colors != null)
			listColor.addAll(Arrays.asList(colors));
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
