package de.lars.remotelightclient.animation.animations;

import java.awt.Color;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.animation.Animation;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.utils.RainbowWheel;

public class TheaterChase extends Animation {
	
	private boolean swtch;
	private int hsv = 0;
	private int counter = 0;
	private Color[] strip = new Color[Main.getLedNum()];

	public TheaterChase() {
		super("TheaterChase");
	}
	
	@Override
	public void onEnable() {
		for(int i = 0; i < strip.length; i++) {
			strip[i] = Color.BLACK;
		}
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		if(swtch) {
			counter++;
			if(counter == 2) {
				strip[0] = RainbowWheel.getRainbow()[hsv];
				counter = 0;
			} else {
				strip[0] = Color.BLACK;
			}
			
			for(int i = strip.length-1; i > 0; i--) {
				strip[i] = strip[i-1];
			}
			
			hsv++;
			if(hsv >= RainbowWheel.getRainbow().length) {
				hsv = 0;
			}
			
			OutputManager.addToOutput(strip);
		}
		swtch = !swtch;
		
		super.onLoop();
	}
	
}
