package de.lars.remotelightcore.utils.color.palette;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.lars.remotelightcore.utils.color.Color;

public class PaletteParser {
	
	public static PaletteData parseFromString(String code) throws PaletteParseException {
		// remove any comments first
		code = code.replaceAll("/\\*(?:.|[\\n\\r])*?\\*/", "");
		
		// try to parse the name of the palette
		Pattern pattern = Pattern.compile("^(.*)(?=(?:[{]))", Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(code);
		String name = null;
		if(matcher.find()) {
			name = matcher.group(1);
			name = name.trim();
		}
		if(name == null || name.isEmpty()) {
			throw new PaletteParseException("No name defined.");
		}
		
		// try to parse the fractions
		pattern = Pattern.compile("(\\d*\\.?\\d+)[ \\t]*(?=([:]))", Pattern.MULTILINE);
		matcher = pattern.matcher(code);
		List<Float> listFractions = new ArrayList<Float>();
		while(matcher.find()) {
			try {
				float fraction = Float.parseFloat(matcher.group(1));
				if(fraction < 0.0f || fraction > 1.0f) {
					throw new PaletteParseException("Fraction numbers must be in range 0..1", matcher.start(), matcher.end());
				}
				listFractions.add(fraction);
			} catch(NumberFormatException e) {
				throw new PaletteParseException("Could not parse fraction number", matcher.start(), matcher.end(), e);
			}
		}
		
		// try to parse the RGB values
		if(listFractions.size() > 0) { // GradientPalette
			pattern = Pattern.compile("(?<=(?:[:]))[ \\t]*(\\d+)[ \\t]*[,][ \\t]*(\\d+)[ \\t]*[,][ \\t]*(\\d+)", Pattern.MULTILINE);
		} else { // EvenGradientPalette
			pattern = Pattern.compile("^[ \\t]*(\\d+)[ \\t]*[,][ \\t]*(\\d+)[ \\t]*[,][ \\t]*(\\d+)", Pattern.MULTILINE);
		}
		matcher = pattern.matcher(code);
		List<Color> listColors = new ArrayList<Color>();
		while(matcher.find()) {
			try {
				int r = Integer.parseInt(matcher.group(1));
				int g = Integer.parseInt(matcher.group(2));
				int b = Integer.parseInt(matcher.group(3));
				listColors.add(new Color(r, g, b));
			} catch(IndexOutOfBoundsException | IllegalArgumentException e) {
				throw new PaletteParseException("Could not parse color values", matcher.start(), matcher.end(), e);
			}
		}
		
		if(listColors.size() < 2) {
			throw new PaletteParseException("You need to define at least 2 colors.");
		}
		
		PaletteData paletteData = new PaletteData(name, null);
		
		// create the color palette
		if(listFractions.size() > 0) { // GradientPalette
			if(listFractions.size() != listColors.size()) {
				throw new PaletteParseException("Number of colors and fractions does not match.");
			}
			GradientPalette gp = new GradientPalette();
			for(int i = 0; i < listFractions.size(); i++) {
				try {
					gp.add(listFractions.get(i), listColors.get(i));
				} catch(IllegalArgumentException e) {
					throw new PaletteParseException("Could not create GradientPalette.", e);
				}
			}
			paletteData.setPalette(gp);
		} else if(listColors.size() > 0) { // EvenGradientPalette
			EvenGradientPalette ep = new EvenGradientPalette(0.05f, listColors.toArray(new Color[0]));
			paletteData.setPalette(ep);
		} else {
			throw new PaletteParseException("No color values found.");
		}
		
		return paletteData;
	}
	
	
	public static String parseToString(PaletteData paletteData) throws PaletteParseException {
		StringBuilder sb = new StringBuilder();
		
		sb.append(paletteData.getName() + " {");
		
		if(paletteData.getPalette() instanceof GradientPalette) {
			GradientPalette gp = (GradientPalette) paletteData.getPalette();
			List<Float> fractions = gp.getPositions();
			List<Color> colors = gp.getColors();
			for(int i = 0; i < fractions.size(); i++) {
				Color color = colors.get(i);
				sb.append(String.format("\n%s: %d,%d,%d",
						fractions.get(i).floatValue(),
						color.getRed(), color.getGreen(), color.getBlue()));
			}
		} else if(paletteData.getPalette() instanceof ColorPalette) {
			ColorPalette cp = (ColorPalette) paletteData.getPalette();
			for(int i = 0; i < cp.size(); i++) {
				Color color = cp.getColorAtIndex(i);
				sb.append(String.format("\n%d,%d,%d",
						color.getRed(), color.getGreen(), color.getBlue()));
			}
		} else {
			throw new PaletteParseException("Unsupported color palette type.");
		}
		
		sb.append("\n}");
		return sb.toString();
	}
	
	
	public static class PaletteParseException extends Exception {
		private static final long serialVersionUID = -5733743671547489903L;
		
		/** first character of the group that caused the error */
		private int startChar = -1;
		/** last character of the group that caused the error */
		private int endChar = -1;
		
		public PaletteParseException(String message, Throwable cause) {
			super(message, cause);
		}
		
		public PaletteParseException(String message, int startChar, int endChar, Throwable cause) {
			super(message, cause);
			this.startChar = startChar;
			this.endChar = endChar;
		}
		
		public PaletteParseException(String message, int startChar, int endChar) {
			super(message);
			this.startChar = startChar;
			this.endChar = endChar;
		}
		
		public PaletteParseException(String message) {
			super(message);
		}

		public int getStartChar() {
			return startChar;
		}

		public void setStartChar(int startChar) {
			this.startChar = startChar;
		}

		public int getEndChar() {
			return endChar;
		}

		public void setEndChar(int endChar) {
			this.endChar = endChar;
		}
	}

}
