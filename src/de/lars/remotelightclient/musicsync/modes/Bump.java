package de.lars.remotelightclient.musicsync.modes;

import java.awt.Color;

import de.lars.remotelightclient.musicsync.MusicEffect;
import de.lars.remotelightclient.musicsync.MusicSyncUtils;
import de.lars.remotelightclient.utils.PixelColorUtils;

public class Bump extends MusicEffect {
	
	private Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.YELLOW, Color.MAGENTA, Color.WHITE, Color.PINK};
	private int color = 0;
	private Color c = Color.black;

	public Bump() {
		super("Bump");
	}
	
	@Override
	public void onLoop() {
		PixelColorUtils.shiftRight(3);
		
		if(this.isBump()) {
			if(color < colors.length - 1) {
				color++;
			}
			else color = 0;
			
			c = colors[color];
			
			for(int i = 0; i < 3; i++) {
				PixelColorUtils.setPixel(i, c);
			}
			
		} else {
			Color d = MusicSyncUtils.dimColor(c, 245);
			
			for(int i = 0; i < 3; i++) {
				PixelColorUtils.setPixel(i, d);
			}
		}
		super.onLoop();
	}

}
