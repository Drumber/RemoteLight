package de.lars.remotelightcore.utils.color;

import java.io.Serializable;

/**
 * Color class to replace the {@link java.awt.Color} class.
 */
public class Color implements Serializable {

	private static final long serialVersionUID = 2820702792432338448L;

	public static final Color white = new Color(255, 255, 255);

	public static final Color WHITE = white;

	public static final Color lightGray = new Color(192, 192, 192);

	public static final Color LIGHT_GRAY = lightGray;

	public static final Color gray = new Color(128, 128, 128);

	public static final Color GRAY = gray;

	public static final Color darkGray = new Color(64, 64, 64);

	public static final Color DARK_GRAY = darkGray;

	public static final Color black = new Color(0, 0, 0);

	public static final Color BLACK = black;

	public static final Color red = new Color(255, 0, 0);

	public static final Color RED = red;

	public static final Color pink = new Color(255, 175, 175);

	public static final Color PINK = pink;

	public static final Color orange = new Color(255, 200, 0);

	public static final Color ORANGE = orange;

	public static final Color yellow = new Color(255, 255, 0);

	public static final Color YELLOW = yellow;

	public static final Color green = new Color(0, 255, 0);

	public static final Color GREEN = green;

	public static final Color magenta = new Color(255, 0, 255);

	public static final Color MAGENTA = magenta;

	public static final Color cyan = new Color(0, 255, 255);

	public static final Color CYAN = cyan;

	public static final Color blue = new Color(0, 0, 255);

	public static final Color BLUE = blue;

	protected int value;

	/**
	 * Check whether the color RGB-values are within the range {@code 0} to
	 * {@code 255}.
	 * 
	 * @param r Red color value
	 * @param g Green color value
	 * @param b Blue color value
	 * @throws IllegalArgumentException if one value is out of range.
	 */
	protected static void testValueRange(int r, int g, int b) {
		boolean isError = false;
		String errorMessage = "Color values must be in range 0..255:";
		if (r < 0 || r > 255) {
			isError = true;
			errorMessage += " Red";
		}
		if (g < 0 || g > 255) {
			isError = true;
			errorMessage += " Green";
		}
		if (b < 0 || b > 255) {
			isError = true;
			errorMessage += " Green";
		}

		if (isError)
			throw new IllegalArgumentException(errorMessage);
	}

	/**
	 * Creates a Color object using the specified red, green and blue values. The
	 * values must be in the range 0..255.
	 * 
	 * @param r Red color value
	 * @param g Green color value
	 * @param b Blue color value
	 * @throws IllegalArgumentException if one value is out of range.
	 */
	public Color(int r, int g, int b) {
		value = ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0);
		testValueRange(r, g, b);
	}

	/**
	 * Creates an RGB Color object using the specified combined RGB value. The red
	 * value must be in bits 16-23, green in bits 8-15 and blue in bits 0-7.
	 * 
	 * @param rgb combined RGB value
	 */
	public Color(int rgb) {
		value = 0xFF000000 | rgb;
	}

	/**
	 * Creates an RGB Color object using the specified HSB values.
	 * 
	 * @param hue        value in range 0.0-1.0
	 * @param saturation value in range 0.0-1.0
	 * @param brightness value in range 0.0-1.0
	 * @see Color#HSBtoRGB
	 */
	public Color(float hue, float saturation, float brightness) {
		value = HSBtoRGB(hue, saturation, brightness);
	}

	/**
	 * Returns the combined RGB value. Red goes from bits 16-23, green from bits
	 * 8-15 and blue from bits 0-7.
	 * 
	 * @return combined RGB value
	 */
	public int getRGB() {
		return value;
	}

	/**
	 * Returns the red value in the range 0-255.
	 * 
	 * @return red value
	 */
	public int getRed() {
		return (getRGB() >> 16) & 0xFF;
	}

	/**
	 * Returns the green value in the range 0-255.
	 * 
	 * @return green value
	 */
	public int getGreen() {
		return (getRGB() >> 8) & 0xFF;
	}

	/**
	 * Returns the blue value in the range 0-255.
	 * 
	 * @return blue value
	 */
	public int getBlue() {
		return (getRGB() >> 0) & 0xFF;
	}

	/**
	 * Gets the HSB values of this Color object.
	 * 
	 * @return float array including hue, saturation and brightness
	 * @see Color#RGBtoHSB
	 */
	public float[] getHSBValues() {
		return Color.RGBtoHSB(getRed(), getGreen(), getBlue(), null);
	}

	/**
	 * Decode Color from a String representing a hexadecimal RGB color.
	 * 
	 * @param hexString hex 24-bit integer as String
	 * @return a new {@code Color} object
	 * @see java.lang.Integer#decode
	 * @throws NumberFormatException if the specified String is not a valid
	 *                               hexadecimal number.
	 */
	public static Color decodeHex(String hexString) {
		int hex = Integer.decode(hexString).intValue();
		return new Color((hex >> 16) & 0xFF, (hex >> 8) & 0xFF, hex & 0xFF);
	}

	@Override
	public String toString() {
		return getClass().getName() + "[r=" + getRed() + ",g=" + getGreen() + ",b=" + getBlue() + "]";
	}

	public String toSimpleString() {
		return "r=" + getRed() + ",g=" + getGreen() + ",b=" + getBlue();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Color && ((Color) obj).getRGB() == this.getRGB();
	}

	@Override
	public int hashCode() {
		return value;
	}

	/**
	 * <b>Code taken from the {@link java.awt.Color} class.</b>
	 * <p>
	 * Converts the components of a color, as specified by the HSB model, to an
	 * equivalent set of values for the default RGB model.
	 * <p>
	 * The {@code saturation} and {@code brightness} components should be
	 * floating-point values between zero and one (numbers in the range 0.0-1.0).
	 * The {@code hue} component can be any floating-point number. The floor of this
	 * number is subtracted from it to create a fraction between 0 and 1. This
	 * fractional number is then multiplied by 360 to produce the hue angle in the
	 * HSB color model.
	 * <p>
	 * The integer that is returned by {@code HSBtoRGB} encodes the value of a color
	 * in bits 0-23 of an integer value that is the same format used by the method
	 * {@link #getRGB() getRGB}. This integer can be supplied as an argument to the
	 * {@code Color} constructor that takes a single integer argument.
	 * 
	 * @param hue        the hue component of the color
	 * @param saturation the saturation of the color
	 * @param brightness the brightness of the color
	 * @return the RGB value of the color with the indicated hue, saturation, and
	 *         brightness.
	 */
	public static int HSBtoRGB(float hue, float saturation, float brightness) {
		int r = 0, g = 0, b = 0;
		if (saturation == 0) {
			r = g = b = (int) (brightness * 255.0f + 0.5f);
		} else {
			float h = (hue - (float) Math.floor(hue)) * 6.0f;
			float f = h - (float) java.lang.Math.floor(h);
			float p = brightness * (1.0f - saturation);
			float q = brightness * (1.0f - saturation * f);
			float t = brightness * (1.0f - (saturation * (1.0f - f)));
			switch ((int) h) {
			case 0:
				r = (int) (brightness * 255.0f + 0.5f);
				g = (int) (t * 255.0f + 0.5f);
				b = (int) (p * 255.0f + 0.5f);
				break;
			case 1:
				r = (int) (q * 255.0f + 0.5f);
				g = (int) (brightness * 255.0f + 0.5f);
				b = (int) (p * 255.0f + 0.5f);
				break;
			case 2:
				r = (int) (p * 255.0f + 0.5f);
				g = (int) (brightness * 255.0f + 0.5f);
				b = (int) (t * 255.0f + 0.5f);
				break;
			case 3:
				r = (int) (p * 255.0f + 0.5f);
				g = (int) (q * 255.0f + 0.5f);
				b = (int) (brightness * 255.0f + 0.5f);
				break;
			case 4:
				r = (int) (t * 255.0f + 0.5f);
				g = (int) (p * 255.0f + 0.5f);
				b = (int) (brightness * 255.0f + 0.5f);
				break;
			case 5:
				r = (int) (brightness * 255.0f + 0.5f);
				g = (int) (p * 255.0f + 0.5f);
				b = (int) (q * 255.0f + 0.5f);
				break;
			}
		}
		return 0xff000000 | (r << 16) | (g << 8) | (b << 0);
	}

	/**
	 * <b>Code taken from the {@link java.awt.Color} class.</b>
	 * <p>
	 * Converts the components of a color, as specified by the default RGB model, to
	 * an equivalent set of values for hue, saturation, and brightness that are the
	 * three components of the HSB model.
	 * <p>
	 * If the {@code hsbvals} argument is {@code null}, then a new array is
	 * allocated to return the result. Otherwise, the method returns the array
	 * {@code hsbvals}, with the values put into that array.
	 * 
	 * @param r       the red component of the color
	 * @param g       the green component of the color
	 * @param b       the blue component of the color
	 * @param hsbvals the array used to return the three HSB values, or {@code null}
	 * @return an array of three elements containing the hue, saturation, and
	 *         brightness (in that order), of the color with the indicated red,
	 *         green, and blue components.
	 */
	public static float[] RGBtoHSB(int r, int g, int b, float[] hsbvals) {
		float hue, saturation, brightness;
		if (hsbvals == null) {
			hsbvals = new float[3];
		}
		int cmax = (r > g) ? r : g;
		if (b > cmax)
			cmax = b;
		int cmin = (r < g) ? r : g;
		if (b < cmin)
			cmin = b;

		brightness = ((float) cmax) / 255.0f;
		if (cmax != 0)
			saturation = ((float) (cmax - cmin)) / ((float) cmax);
		else
			saturation = 0;
		if (saturation == 0)
			hue = 0;
		else {
			float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
			float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
			float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
			if (r == cmax)
				hue = bluec - greenc;
			else if (g == cmax)
				hue = 2.0f + redc - bluec;
			else
				hue = 4.0f + greenc - redc;
			hue = hue / 6.0f;
			if (hue < 0)
				hue = hue + 1.0f;
		}
		hsbvals[0] = hue;
		hsbvals[1] = saturation;
		hsbvals[2] = brightness;
		return hsbvals;
	}

	/**
	 * Create a Color from the specified HSB values (hue, saturation and brightness)
	 * 
	 * @param h hue in range 0.0-1.0
	 * @param s saturation in range 0.0-1.0
	 * @param b brightness in range 0.0-1.0
	 * @return a new Color object
	 * @see Color#HSBtoRGB
	 */
	public static Color getHSBColor(float h, float s, float b) {
		return new Color(HSBtoRGB(h, s, b));
	}

	/**
	 * <b>Code adapted from the {@link java.awt.Color} class.</b>
	 * <p>
	 * Returns a {@code float} array containing only the color components of the
	 * {@code Color}, in the default sRGB color space. If {@code compArray} is
	 * {@code null}, an array of length 3 is created for the return value.
	 * Otherwise, {@code compArray} must have length 3 or greater, and it is filled
	 * in with the components and returned.
	 * 
	 * @param compArray an array that this method fills with color components and
	 *                  returns
	 * @return the RGB components in a {@code float} array.
	 */
	public float[] getRGBColorComponents(float[] compArray) {
		float[] f;
		if (compArray == null) {
			f = new float[3];
		} else {
			f = compArray;
		}
		f[0] = ((float) getRed()) / 255f;
		f[1] = ((float) getGreen()) / 255f;
		f[2] = ((float) getBlue()) / 255f;
		return f;
	}

}
