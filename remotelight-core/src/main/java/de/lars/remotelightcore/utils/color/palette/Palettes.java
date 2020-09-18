package de.lars.remotelightcore.utils.color.palette;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Palettes {
	
	private static final Map<String, AbstractPalette> listPalette = new HashMap<String, AbstractPalette>();
	
	public static Map<String, AbstractPalette> getAll() {
		return listPalette;
	}
	
	public static Set<String> getNames() {
		return getAll().keySet();
	}
	
	public static AbstractPalette getPalette(String name) {
		return getAll().get(name);
	}
	
	/**
	 * Add color palette to the list of default palettes
	 * @param <T>
	 * @param palette		palette to add
	 * @return				the added palette
	 */
	private static <T extends AbstractPalette> T put(String name, T palette) {
		listPalette.put(name, palette);
		return palette;
	}
	
	
	/*
	 * Some default color palettes, most of them from
	 * http://soliton.vm.bytemark.co.uk/pub/cpt-city/ and converted using
	 * http://fastled.io/tools/paletteknife/
	 */

	public static final EvenGradientPalette RAINBOW = put("Rainbow", new EvenGradientPalette(0.05f).add(255, 0, 0) // red
			.add(213, 42, 0).add(171, 85, 0).add(171, 127, 0).add(171, 171, 0).add(86, 213, 0).add(0, 255, 0)
			.add(0, 213, 42).add(0, 171, 85).add(0, 86, 170).add(0, 0, 255).add(42, 0, 213).add(85, 0, 171)
			.add(127, 0, 129).add(171, 0, 85).add(213, 0, 43));

	public static final GradientPalette RAINBOW2 = put("Rainbow2", new GradientPalette().add(0, 126, 1, 142).add(25, 171, 1, 26)
			.add(48, 224, 9, 1).add(71, 237, 138, 1).add(94, 52, 173, 1).add(117, 1, 201, 1).add(140, 1, 211, 54)
			.add(163, 1, 124, 168).add(186, 1, 8, 149).add(209, 12, 1, 151).add(232, 12, 1, 151).add(255, 171, 1, 190));

	public static final GradientPalette AUTUMNROSE = put("Autumn Rose", new GradientPalette().add(0, 71, 3, 1).add(45, 128, 5, 2)
			.add(84, 186, 11, 3).add(127, 215, 27, 8).add(153, 224, 69, 13).add(188, 229, 84, 6).add(226, 242, 135, 17)
			.add(255, 247, 161, 79));

	public static final GradientPalette OTIS = put("Otis", new GradientPalette().add(0, 26, 1, 89).add(127, 17, 193, 0)
			.add(216, 0, 34, 98).add(255, 0, 34, 98));
	

}
