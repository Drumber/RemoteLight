package de.lars.remotelightclient.animation.animations;

import java.awt.Color;

import de.lars.remotelightclient.animation.Animation;
import de.lars.remotelightclient.arduino.Arduino;

public class Wipe extends Animation {
	
	private Color[] colors;
	Color c;
	Color[] strip;
	int pix, count;
	boolean wiping;

	public Wipe(String name, String displayname) {
		super(name, displayname);
	}
	
	@Override
	public void onEnable() {
		Color[] colors = {Color.RED, Color.ORANGE, Color.PINK, Color.MAGENTA,
	    		Color.BLUE, Color.CYAN, Color.GREEN};
		this.colors = colors;
		c = Color.RED;
		strip = Arduino.getStrip();
		pix = 0; count = 0;
		wiping = false;
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		if(!wiping) {
			count++;
			if(count >= colors.length) count = 0;
			c = colors[count];
			Arduino.setColorPixel(0, c);
			pix = 0;
			wiping = true;
		} else {
			pix++;
			if(pix < strip.length) {
				Arduino.setColorPixel(pix, c);
			} else {
				wiping = false;
			}
		}
		super.onLoop();
	}

}
