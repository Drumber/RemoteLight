package de.lars.remotelightclient.musicsync.ws281x;

import java.awt.Color;


import de.lars.remotelightclient.network.Client;
import de.lars.remotelightclient.network.Identifier;

public class Bump {
	
	private static Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.YELLOW, Color.MAGENTA, Color.WHITE, Color.PINK};
	private static int color = 0;
	private static Color c = Color.black;
	
	public static void bump(boolean bump) {
		if(bump) {
			if(color < colors.length - 1) {
				color++;
			}
			else color = 0;
			
			c = colors[color];
			
			Client.send(new String[] {Identifier.WS_SHIFT_RIGHT, 3+""});
			for(int i = 0; i < 3; i++) {
				Client.send(new String[] {Identifier.WS_COLOR_PIXEL, i+"", c.getRed()+"", c.getGreen()+"", c.getBlue()+""});
			}
			
		} else {
			Color d = dimColor(c, 245);
			
			Client.send(new String[] {Identifier.WS_SHIFT_RIGHT, 3+""});
			for(int i = 0; i < 3; i++) {
				Client.send(new String[] {Identifier.WS_COLOR_PIXEL, i+"", d.getRed()+"", d.getGreen()+"", d.getBlue()+""});
			}
		}
	}
	
	private static Color dimColor(Color color, int dimValue) {
		int r = color.getRed() - dimValue;
		int g = color.getGreen() - dimValue;
		int b = color.getBlue() - dimValue;
		if(r < 0) r = 0;
		if(g < 0) g = 0;
		if(b < 0) b = 0;
		return new Color(r, g, b);
	}

}
