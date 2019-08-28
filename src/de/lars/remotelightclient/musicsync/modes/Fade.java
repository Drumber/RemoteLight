package de.lars.remotelightclient.musicsync.modes;

import java.awt.Color;

import de.lars.remotelightclient.musicsync.MusicEffect;
import de.lars.remotelightclient.musicsync.MusicSyncUtils;
import de.lars.remotelightclient.network.Client;
import de.lars.remotelightclient.network.Identifier;

public class Fade extends MusicEffect {
	
	private Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.YELLOW, Color.MAGENTA, Color.WHITE, Color.PINK};
	private Color fadeLastColor = colors[0];
	private int color = 0;
	
	public Fade() {
		super("Fade");
	}
	
	@Override
	public void onLoop() {
		if(this.isBump()) {
			if(color < colors.length - 1) {
				color++;
			}
			else color = 0;
			fadeLastColor = colors[color];
		}
		Client.send(new String[] {Identifier.COLOR_COLOR, fadeLastColor.getRed()+"", fadeLastColor.getGreen()+"", fadeLastColor.getBlue()+""});
		
		if((fadeLastColor.getRed() != 0) || (fadeLastColor.getGreen() != 0) || (fadeLastColor.getBlue() != 0)) fadeLastColor = MusicSyncUtils.dimColor(fadeLastColor, 2);
		else {
			if(color < colors.length - 1) {
				color++;
			}
			else color = 0;
			fadeLastColor = colors[color];
		}
		super.onLoop();
	}

}
