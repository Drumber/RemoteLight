package de.lars.remotelightcore.devices.link.multi;

/**
 * Different methods for splitting the colors on several different strip lengths.
 */
public enum DividingMethod {
	
	/**
	 * Take the largest number of pixels and
	 * cut off remaining colors for smaller strips.
	 */
	CUT_OVERHANGING("Pixel number corresponds to the largest strip. If the strip is smaller, the leftover pixels are cut off."),
	
	/**
	 * Just like {@link #CUT_OVERHANGING} but
	 * center colors on smaller strips.
	 */
	CUT_OVERHANGING_CENTER("Pixel number corresponds to the largest strip. If the strip is smaller, center and cuf off leftover pixels."),
	
	/**
	 * Take the smallest number of pixels and
	 * black out remaining pixels for larger strips.
	 */
	BLACK_OVERHANGING("Pixel number corresponds to the smallest strip. If the strip is larger, the empty space is filled with black pixels."),
	
	/**
	 * Just like {@link #BLACK_OVERHANGING} but
	 * center colors on larger strips.
	 */
	BLACK_OVERHANGING_CENTER("Pixel number corresponds to the smallest strip. If the strip is larger, the empty space around the center is filled with black pixels.");
	
	private final String description;
	
	private DividingMethod(String desc) {
		description = desc;
	}
	
	public String getDescription() {
		return description;
	}

}
