package de.lars.remotelightclient.animation.animations;

import java.awt.Color;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.animation.Animation;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.utils.ColorUtil;
import de.lars.remotelightclient.utils.PixelColorUtils;
import de.lars.remotelightclient.utils.RainbowWheel;

public class Breath extends Animation {
	
	private int brghtns = 100;
	private Color color;
	private boolean darker = true;
	private int nxtColorCounter = 0;

	public Breath() {
		super("Breath");
		color = RainbowWheel.getRandomColor();
	}
	
	@Override
	public void onLoop() {
		Color tmp = color;
		
		if(darker) {
			if(--brghtns <= 5) {
				darker = false;
			}
		} else {
			if(++brghtns >= 100) {
				darker = true;
				if(++nxtColorCounter == 3) {
					color = RainbowWheel.getRandomColor();
					nxtColorCounter = 0;
				}
			}
		}
		tmp = ColorUtil.dimColor(tmp, brghtns);
		OutputManager.addToOutput(PixelColorUtils.colorAllPixels(tmp, Main.getLedNum()));
		super.onLoop();
	}

}
