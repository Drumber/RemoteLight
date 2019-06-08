package de.lars.remotelightclient.arduino;

import java.awt.Color;

/*
 * adapted from https://stackoverflow.com/a/52498075
 */

public class RainbowWheel {
	
	private final static int SIZE = 360;
	private static Color[] rainbow = new Color[SIZE];
	
	public static void init() {
		double jump = 1.0 / (SIZE * 1.0);
		int[] colors = new int[SIZE];
		for(int i = 0; i < colors.length; i++) {
			colors[i] = Color.HSBtoRGB((float) (jump * i), 1.0f, 1.0f);
			rainbow[i] = Color.getHSBColor((float) (jump * i), 1.0f, 1.0f);
		}
	}
	
	public static Color[] getRainbow() {
		return rainbow;
	}

}
