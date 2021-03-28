package de.lars.remotelightcore.utils.color.palette;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Palettes {

	private static final Set<PaletteData> listDefaultPalette = new LinkedHashSet<PaletteData>();
	
	private static final Set<PaletteData> listPalette = new LinkedHashSet<PaletteData>();

	public static Set<PaletteData> getDefaults() {
		return listDefaultPalette;
	}
	
	/**
	 * Clear the palette list and add the default palettes.
	 * <br>
	 * <b>User defined palette data will be lost!</b>
	 */
	public static void restoreDefaults() {
		listPalette.clear();
		listPalette.addAll(listDefaultPalette);
	}
	
	public static Set<PaletteData> getAll() {
		return listPalette;
	}

	/**
	 * Get a list of all unique palette names.
	 * @return			Set of palette names
	 */
	public static Set<String> getNames() {
		return getAll().stream()
				.map(pd -> pd.getName())
				.collect(Collectors.toSet());
	}

	/**
	 * Get the palette data with the specified name.
	 * @param name		the name to search for
	 * @return			{@link PaletteData} or
	 * 					{@code null} if palette does not exist
	 */
	public static PaletteData getPalette(String name) {
		return getAll().stream()
				.filter(pd -> name.equals(pd.getName()))
				.findFirst()
				.orElse(null);
	}
	
	/**
	 * Add a new palette to the list. The palette must have a unique
	 * name which does not already exist.
	 * @param paletteData	new palette to add
	 * @return				{@code null} if the PaletteData or name does
	 * 						already exist.
	 */
	public static PaletteData addPalette(PaletteData paletteData) {
		if(getPalette(paletteData.getName()) == null && listPalette.add(paletteData)) {
			return paletteData;
		}
		return null;
	}
	
	/**
	 * Remove the specified palette from the list.
	 * @param palette		the palette to remove
	 * @return				true if the palette could be removed
	 */
	public static boolean removePalette(PaletteData palette) {
		return listPalette.remove(palette);
	}


	/**
	 * Add color palette to the list of default palettes
	 * 
	 * @param <T>
	 * @param palette palette to add
	 * @return the added palette
	 */
	private static <T extends AbstractPalette> T putDefault(String name, T palette) {
		listDefaultPalette.add(new PaletteData(name, palette));
		return palette;
	}

	/*
	 * Some default color palettes, most of them from
	 * http://soliton.vm.bytemark.co.uk/pub/cpt-city/ and converted using
	 * http://fastled.io/tools/paletteknife/
	 */

	public static final EvenGradientPalette RAINBOW = putDefault("Rainbow",
			new EvenGradientPalette(0.05f).add(255, 0, 0).add(213, 42, 0).add(171, 85, 0).add(171, 127, 0)
					.add(171, 171, 0).add(86, 213, 0).add(0, 255, 0).add(0, 213, 42).add(0, 171, 85).add(0, 86, 170)
					.add(0, 0, 255).add(42, 0, 213).add(85, 0, 171).add(127, 0, 129).add(171, 0, 85).add(213, 0, 43));

	// --------------------
	// Gradients from Restless Concepts
	// http://soliton.vm.bytemark.co.uk/pub/cpt-city/rc/
	// --------------------

	public static final GradientPalette RAINBOW2 = putDefault("Rainbow2",
			new GradientPalette().add(0, 126, 1, 142).add(25, 171, 1, 26).add(48, 224, 9, 1).add(71, 237, 138, 1)
					.add(94, 52, 173, 1).add(117, 1, 201, 1).add(140, 1, 211, 54).add(163, 1, 124, 168)
					.add(186, 1, 8, 149).add(209, 12, 1, 151).add(232, 12, 1, 151).add(255, 171, 1, 190));

	public static final GradientPalette AUTUMNROSE = putDefault("Autumn Rose",
			new GradientPalette().add(0, 71, 3, 1).add(45, 128, 5, 2).add(84, 186, 11, 3).add(127, 215, 27, 8)
					.add(153, 224, 69, 13).add(188, 229, 84, 6).add(226, 242, 135, 17).add(255, 247, 161, 79));

	public static final GradientPalette OTIS = putDefault("Otis",
			new GradientPalette().add(0, 26, 1, 89).add(127, 17, 193, 0).add(216, 0, 34, 98).add(255, 0, 34, 98));

	public static final GradientPalette PURPLEFLY = putDefault("Purple Fly",
			new GradientPalette().add(0, 0, 0, 0).add(63, 239, 0, 122).add(191, 252, 255, 78).add(255, 0, 0, 0));

	public static final GradientPalette BLUEFLY = putDefault("Blue Fly",
			new GradientPalette().add(0, 0, 0, 0).add(63, 0, 39, 64).add(191, 175, 215, 235).add(255, 0, 0, 0));

	public static final GradientPalette CLOUD = putDefault("Cloud",
			new GradientPalette().add(0, 247, 149, 91).add(127, 208, 32, 71).add(255, 42, 79, 188));

	public static final GradientPalette HALLOWEEN = putDefault("Halloween",
			new GradientPalette().add(0, 173, 53, 1).add(127, 0, 0, 0).add(191, 173, 53, 1).add(255, 173, 53, 1));

	public static final GradientPalette TEABEARROSE = putDefault("Tea Bear Rose",
			new GradientPalette().add(0, 107, 1, 5).add(25, 165, 25, 45).add(38, 184, 82, 88).add(63, 229, 133, 130)
					.add(89, 229, 133, 130).add(109, 186, 40, 4).add(117, 215, 139, 96).add(122, 148, 8, 1)
					.add(127, 215, 139, 96).add(132, 148, 8, 1).add(137, 215, 139, 96).add(145, 186, 40, 4)
					.add(165, 229, 133, 130).add(191, 229, 133, 130).add(216, 184, 82, 88).add(229, 165, 25, 45)
					.add(255, 107, 1, 5));

	public static final GradientPalette MISTRESSNIGHT = putDefault("Mistress Night", new GradientPalette().add(0, 1, 5, 29)
			.add(51, 36, 10, 23).add(127, 118, 91, 17).add(204, 36, 10, 23).add(255, 1, 4, 28));

	public static final GradientPalette SPELLBOUND = putDefault("Spellbound", new GradientPalette().add(0.00f, 246, 246, 122)
			.add(0.05f, 212, 252, 129).add(0.10f, 178, 251, 134).add(0.18f, 140, 253, 113).add(0.25f, 93, 247, 137)
			.add(0.32f, 77, 234, 213).add(0.37f, 93, 199, 238).add(0.40f, 129, 184, 248).add(0.44f, 136, 147, 252)
			.add(0.50f, 136, 147, 252).add(0.55f, 173, 172, 252).add(0.59f, 191, 186, 253).add(0.64f, 197, 193, 254)
			.add(0.68f, 205, 189, 254).add(0.73f, 207, 176, 254).add(0.77f, 205, 107, 220).add(0.83f, 187, 77, 166)
			.add(0.87f, 215, 101, 137).add(0.92f, 255, 168, 159).add(0.97f, 245, 246, 129).add(1.00f, 245, 246, 129));

	public static final GradientPalette FLAME = putDefault("Flame",
			new GradientPalette().add(0.00f, 254, 113, 23).add(0.17f, 240, 47, 4).add(0.35f, 238, 138, 26)
					.add(0.50f, 48, 146, 11).add(0.65f, 238, 138, 26).add(0.83f, 240, 47, 4).add(1.00f, 254, 113, 23));

	public static final GradientPalette BUTTERFLYFAIRY = putDefault("Butterfly Fairy",
			new GradientPalette().add(0.00f, 167, 77, 38).add(0.25f, 215, 167, 105).add(0.50f, 133, 203, 206)
					.add(0.75f, 19, 88, 138).add(1.00f, 1, 6, 9));

	public static final GradientPalette HANGONFATBOY = putDefault("Hang on fatboy",
			new GradientPalette().add(0.00f, 239, 146, 58).add(0.12f, 159, 85, 137).add(0.24f, 93, 47, 104)
					.add(0.38f, 59, 106, 165).add(0.45f, 6, 53, 109).add(0.55f, 6, 53, 109).add(0.62f, 59, 106, 165)
					.add(0.76f, 93, 47, 104).add(0.88f, 159, 85, 137).add(1.00f, 239, 146, 58));

	public static final GradientPalette SUNLITWAVE = putDefault("Sunlit Wave",
			new GradientPalette().add(0.00f, 59, 57, 164).add(0.18f, 122, 88, 183).add(0.32f, 89, 45, 142)
					.add(0.44f, 104, 28, 93).add(0.59f, 118, 23, 43).add(0.78f, 221, 106, 62).add(0.93f, 236, 170, 86)
					.add(1.00f, 249, 234, 211));

	public static final GradientPalette TROVE = putDefault("Trove",
			new GradientPalette().add(0.00f, 81, 87, 74).add(0.05f, 68, 124, 105).add(0.10f, 116, 196, 147)
					.add(0.15f, 142, 140, 109).add(0.20f, 228, 191, 128).add(0.25f, 233, 215, 142)
					.add(0.30f, 226, 151, 93).add(0.35f, 241, 150, 112).add(0.40f, 225, 101, 82).add(0.45f, 201, 74, 83)
					.add(0.50f, 190, 81, 104).add(0.55f, 163, 73, 116).add(0.60f, 153, 55, 103).add(0.65f, 101, 56, 125)
					.add(0.70f, 78, 36, 114).add(0.75f, 145, 99, 182).add(0.80f, 226, 121, 163).add(0.85f, 224, 89, 139)
					.add(0.90f, 124, 159, 176).add(0.95f, 86, 152, 196).add(1.00f, 154, 191, 136));

	public static final GradientPalette RENMAIDEN = putDefault("Ren Maiden",
			new GradientPalette().add(0.00f, 50, 153, 52).add(0.15f, 71, 66, 85).add(0.35f, 225, 148, 179)
					.add(0.50f, 233, 235, 210).add(0.65f, 242, 158, 217).add(0.85f, 228, 136, 51)
					.add(1.00f, 50, 153, 52));
	

	// --------------------
	// Gradients from The Open Clip Art Library
	// http://soliton.vm.bytemark.co.uk/pub/cpt-city/ocal/index.html
	// --------------------

	public static final GradientPalette CHRISTMAS_CANDY = putDefault("Christmas Candy",
			new GradientPalette().add(0, 255, 255, 255).add(25, 255, 0, 0).add(51, 255, 255, 255).add(76, 0, 55, 0)
					.add(102, 255, 255, 255).add(127, 255, 0, 0).add(153, 255, 255, 255).add(178, 0, 55, 0)
					.add(204, 255, 255, 255).add(229, 255, 0, 0).add(255, 255, 255, 255));

	public static final GradientPalette YELLOW_RED_YELLOW = putDefault("Yellow Red Yellow",
			new GradientPalette().add(0, 255, 255, 0).add(127, 255, 0, 0).add(255, 255, 255, 0));

	public static final GradientPalette AQUABLUE_BLUE = putDefault("Aqua-Blue Blue",
			new GradientPalette().add(0, 0, 255, 255).add(255, 0, 0, 255));

	public static final GradientPalette METAL_GOLD = putDefault("Metal Gold",
			new GradientPalette().add(0, 121, 55, 0).add(51, 255, 255, 212).add(102, 255, 156, 6).add(153, 121, 55, 0)
					.add(204, 121, 119, 52).add(255, 255, 156, 6));

	public static final GradientPalette SPECTRUM = putDefault("Spectrum",
			new GradientPalette().add(0.00f, 255, 0, 0).add(0.17f, 255, 165, 0).add(0.33f, 255, 255, 0)
					.add(0.50f, 0, 255, 0).add(0.67f, 0, 255, 255).add(0.83f, 75, 0, 130).add(1.00f, 238, 130, 238));

	public static final GradientPalette PLUM = putDefault("Plum",
			new GradientPalette().add(0.00f, 221, 160, 221).add(0.20f, 255, 128, 128).add(0.40f, 221, 160, 221)
					.add(0.60f, 221, 160, 221).add(0.80f, 255, 128, 128).add(1.00f, 221, 160, 221));
	
}
