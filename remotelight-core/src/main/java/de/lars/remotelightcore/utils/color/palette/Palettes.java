package de.lars.remotelightcore.utils.color.palette;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Palettes {

	private static final Map<String, AbstractPalette> listPalette = new LinkedHashMap<String, AbstractPalette>();

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
	 * 
	 * @param <T>
	 * @param palette palette to add
	 * @return the added palette
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

	public static final EvenGradientPalette RAINBOW = put("Rainbow",
			new EvenGradientPalette(0.05f).add(255, 0, 0).add(213, 42, 0).add(171, 85, 0).add(171, 127, 0)
					.add(171, 171, 0).add(86, 213, 0).add(0, 255, 0).add(0, 213, 42).add(0, 171, 85).add(0, 86, 170)
					.add(0, 0, 255).add(42, 0, 213).add(85, 0, 171).add(127, 0, 129).add(171, 0, 85).add(213, 0, 43));

	// --------------------
	// Gradients from Restless Concepts
	// http://soliton.vm.bytemark.co.uk/pub/cpt-city/rc/
	// --------------------

	public static final GradientPalette RAINBOW2 = put("Rainbow2",
			new GradientPalette().add(0, 126, 1, 142).add(25, 171, 1, 26).add(48, 224, 9, 1).add(71, 237, 138, 1)
					.add(94, 52, 173, 1).add(117, 1, 201, 1).add(140, 1, 211, 54).add(163, 1, 124, 168)
					.add(186, 1, 8, 149).add(209, 12, 1, 151).add(232, 12, 1, 151).add(255, 171, 1, 190));

	public static final GradientPalette AUTUMNROSE = put("Autumn Rose",
			new GradientPalette().add(0, 71, 3, 1).add(45, 128, 5, 2).add(84, 186, 11, 3).add(127, 215, 27, 8)
					.add(153, 224, 69, 13).add(188, 229, 84, 6).add(226, 242, 135, 17).add(255, 247, 161, 79));

	public static final GradientPalette OTIS = put("Otis",
			new GradientPalette().add(0, 26, 1, 89).add(127, 17, 193, 0).add(216, 0, 34, 98).add(255, 0, 34, 98));

	public static final GradientPalette PURPLEFLY = put("Purple Fly",
			new GradientPalette().add(0, 0, 0, 0).add(63, 239, 0, 122).add(191, 252, 255, 78).add(255, 0, 0, 0));

	public static final GradientPalette BLUEFLY = put("Blue Fly",
			new GradientPalette().add(0, 0, 0, 0).add(63, 0, 39, 64).add(191, 175, 215, 235).add(255, 0, 0, 0));

	public static final GradientPalette CLOUD = put("Cloud",
			new GradientPalette().add(0, 247, 149, 91).add(127, 208, 32, 71).add(255, 42, 79, 188));

	public static final GradientPalette HALLOWEEN = put("Halloween",
			new GradientPalette().add(0, 173, 53, 1).add(127, 0, 0, 0).add(191, 173, 53, 1).add(255, 173, 53, 1));

	public static final GradientPalette TEABEARROSE = put("Tea Bear Rose",
			new GradientPalette().add(0, 107, 1, 5).add(25, 165, 25, 45).add(38, 184, 82, 88).add(63, 229, 133, 130)
					.add(89, 229, 133, 130).add(109, 186, 40, 4).add(117, 215, 139, 96).add(122, 148, 8, 1)
					.add(127, 215, 139, 96).add(132, 148, 8, 1).add(137, 215, 139, 96).add(145, 186, 40, 4)
					.add(165, 229, 133, 130).add(191, 229, 133, 130).add(216, 184, 82, 88).add(229, 165, 25, 45)
					.add(255, 107, 1, 5));

	public static final GradientPalette MISTRESSNIGHT = put("Mistress Night", new GradientPalette().add(0, 1, 5, 29)
			.add(51, 36, 10, 23).add(127, 118, 91, 17).add(204, 36, 10, 23).add(255, 1, 4, 28));

	// --------------------
	// Gradients from The Open Clip Art Library
	// http://soliton.vm.bytemark.co.uk/pub/cpt-city/ocal/index.html
	// --------------------

	public static final GradientPalette CHRISTMAS_CANDY = put("Christmas Candy",
			new GradientPalette().add(0, 255, 255, 255).add(25, 255, 0, 0).add(51, 255, 255, 255).add(76, 0, 55, 0)
					.add(102, 255, 255, 255).add(127, 255, 0, 0).add(153, 255, 255, 255).add(178, 0, 55, 0)
					.add(204, 255, 255, 255).add(229, 255, 0, 0).add(255, 255, 255, 255));

	public static final GradientPalette YELLOW_RED_YELLOW = put("Yellow Red Yellow",
			new GradientPalette().add(0, 255, 255, 0).add(127, 255, 0, 0).add(255, 255, 255, 0));

	public static final GradientPalette AQUABLUE_BLUE = put("Aqua-Blue Blue",
			new GradientPalette().add(0, 0, 255, 255).add(255, 0, 0, 255));

	public static final GradientPalette METAL_GOLD = put("Metal Gold",
			new GradientPalette().add(0, 121, 55, 0).add(51, 255, 255, 212).add(102, 255, 156, 6).add(153, 121, 55, 0)
					.add(204, 121, 119, 52).add(255, 255, 156, 6));

}
