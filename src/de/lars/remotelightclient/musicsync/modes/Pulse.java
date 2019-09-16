package de.lars.remotelightclient.musicsync.modes;

import java.awt.Color;
import java.util.Random;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.musicsync.MusicEffect;
import de.lars.remotelightclient.musicsync.MusicSyncUtils;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.utils.PixelColorUtils;

public class Pulse extends MusicEffect {
	
	private int pulseLastHz;
	private Color[] deepColors = {Color.RED, Color.BLUE, new Color(220, 90, 0), new Color(130, 0, 255), new Color(0, 20, 200), new Color(0, 170, 30)};
	private Color[] highColors = {Color.GREEN, Color.CYAN, Color.YELLOW, Color.PINK, Color.WHITE, new Color(255, 220, 90), new Color(0, 255, 200)};
	private Color pulseColor = Color.GREEN;

	public Pulse() {
		super("Pulse");
	}
	
	@Override
	public void onLoop() {
		float pitch = this.getPitch();
		
		int max = (int) (this.getMaxSpl() * 10.);
		int min = (int) (this.getMinSpl() * 10.);
		int spl = (int) (this.getSpl() * 10.);
		int pulseBrightness = 0;
		
		if(this.getMaxSpl() == 0)
			pulseBrightness = 10;
		else if(this.getSpl() == this.getMaxSpl()) {
			pulseBrightness = 255;
		} else {
			try {
				int span = max - min;
				int m = span / (max - spl);
				pulseBrightness = 255 - (255 / m);
				if(pulseBrightness < 0)
					pulseBrightness = 0;
			} catch(ArithmeticException e) {
				pulseBrightness = 200;
			}
		}
		
		//System.out.println("SPL: " + this.spl + " Min/Max: " + minSpl + "/" + maxSpl + " bright: " + pulseBrightness);
		
		int hz = (int) pitch;
		if(hz < pulseLastHz) { //deeper sound
			if((pulseLastHz - hz) > 200) { //if difference between last loop is bigger
				int r = new Random().nextInt(deepColors.length - 1);
				pulseColor = deepColors[r];
			}
		} else if(hz > pulseLastHz) { //higher sound
			if((pulseLastHz - hz) > 200) { //if difference between last loop is bigger
				int r = new Random().nextInt(highColors.length - 1);
				pulseColor = highColors[r];
			}
		}
		pulseLastHz = hz;
		Color c = MusicSyncUtils.changeBrightness(pulseColor, pulseBrightness);
		
		OutputManager.addToOutput(PixelColorUtils.colorAllPixels(c, Main.getLedNum()));
		
		super.onLoop();
	}

}
