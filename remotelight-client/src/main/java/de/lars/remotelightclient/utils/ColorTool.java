package de.lars.remotelightclient.utils;

public class ColorTool {
	
	public static java.awt.Color convert(de.lars.remotelightcore.utils.color.Color rcolor) {
		return new java.awt.Color(rcolor.getRGB());
	}
	
	public static de.lars.remotelightcore.utils.color.Color convert(java.awt.Color rcolor) {
		return new de.lars.remotelightcore.utils.color.Color(rcolor.getRGB());
	}

}
