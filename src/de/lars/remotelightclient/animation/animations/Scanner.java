package de.lars.remotelightclient.animation.animations;

import java.awt.Color;
import java.util.Random;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.animation.Animation;
import de.lars.remotelightclient.utils.PixelColorUtils;

public class Scanner extends Animation {
	
	private Color[] colors;
	private Color c;
	private Color[] strip;
	private int pix;
	private boolean scanning, reverse;

	public Scanner() {
		super("Scanner");
	}
	
	@Override
	public void onEnable() {
		Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.PINK, Color.MAGENTA,
	    		Color.BLUE, Color.CYAN, Color.GREEN};
		this.colors = colors;
		c = Color.RED;
		strip = Main.getInstance().getOutputManager().getLastColors();
		pix = 0;
		scanning = false; reverse = false;
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		if(!scanning) {
			int r = new Random().nextInt(colors.length);
			c = colors[r];
			PixelColorUtils.setPixel(0, c);
			pix = 0;
			scanning = true;
		} else {
			if(!reverse) {
				pix++;
				if(pix < strip.length) {
					PixelColorUtils.setPixel(pix, c);
					PixelColorUtils.setPixel(pix-1, Color.BLACK);
				} else {
					reverse = true;
					pix--;
					PixelColorUtils.setPixel(pix, c);
					PixelColorUtils.setPixel(pix+1, Color.BLACK);
				}
			} else {
				pix--;
				if(pix > 0) {
					PixelColorUtils.setPixel(pix, c);
				} else {
					reverse = false;
					scanning = false;
					PixelColorUtils.setPixel(pix, c);
				}
				PixelColorUtils.setPixel(pix+1, Color.BLACK);
			}
		}
		super.onLoop();
	}

}
