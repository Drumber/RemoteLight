package de.lars.remotelightclient.musicsync.modes;

import java.awt.Color;

import de.lars.remotelightclient.musicsync.MusicEffect;
import de.lars.remotelightclient.musicsync.MusicSyncUtils;
import de.lars.remotelightclient.network.Client;
import de.lars.remotelightclient.network.Identifier;

public class Bump extends MusicEffect {
	
	private Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.YELLOW, Color.MAGENTA, Color.WHITE, Color.PINK};
	private int color = 0;
	private Color c = Color.black;

	public Bump() {
		super("Bump");
	}
	
	@Override
	public void onLoop() {
		if(this.isBump()) {
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
			Color d = MusicSyncUtils.dimColor(c, 245);
			
			Client.send(new String[] {Identifier.WS_SHIFT_RIGHT, 3+""});
			for(int i = 0; i < 3; i++) {
				Client.send(new String[] {Identifier.WS_COLOR_PIXEL, i+"", d.getRed()+"", d.getGreen()+"", d.getBlue()+""});
			}
		}
		super.onLoop();
	}

}
