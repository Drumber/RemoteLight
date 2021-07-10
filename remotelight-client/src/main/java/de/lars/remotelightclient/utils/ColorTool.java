package de.lars.remotelightclient.utils;

import java.awt.Color;

public class ColorTool {
	
	public static java.awt.Color convert(de.lars.remotelightcore.utils.color.Color rcolor) {
		return new java.awt.Color(rcolor.getRGB());
	}
	
	public static de.lars.remotelightcore.utils.color.Color convert(java.awt.Color rcolor) {
		return new de.lars.remotelightcore.utils.color.Color(rcolor.getRGB());
	}
	
	/**
	 * Blending color A over color B using the alpha value from color a.
	 * @param a	the foreground color over b
	 * @param b	the background color under b
	 * @return	the resulting color
	 */
	public static Color alphaBlending(Color a, Color b) {
		float alphaA = a.getAlpha() / 255.0f;
		
		// C = a*A + (1-a)*B
		int cR = (int) Math.min(255, a.getRed()   * alphaA + (1.0f - alphaA) * b.getRed());
		int cG = (int) Math.min(255, a.getGreen() * alphaA + (1.0f - alphaA) * b.getGreen());
		int cB = (int) Math.min(255, a.getBlue()  * alphaA + (1.0f - alphaA) * b.getBlue());
		return new Color(cR, cG, cB);
	}
	
	public static boolean isEqual(Color c1, Color c2) {
		return c1.getRed() == c2.getRed() && c1.getGreen() == c2.getGreen() && c1.getBlue() == c2.getBlue();
	}

}
