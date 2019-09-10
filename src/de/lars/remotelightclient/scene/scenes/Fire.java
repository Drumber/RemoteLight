package de.lars.remotelightclient.scene.scenes;

import java.awt.Color;
import java.util.HashMap;
import java.util.Random;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.scene.Scene;
import de.lars.remotelightclient.utils.PixelColorUtils;

public class Fire extends Scene {
	
	private int pixels;

	public Fire() {
		super("Fire", 100);
	}
	
	@Override
	public void onEnable() {
		pixels = Main.getLedNum();
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		HashMap<Integer, Color> pixelHash = new HashMap<>();
		int r = 255, g = 95, b = 12;
		
		for(int i = 0; i < pixels; i++) {
			
			int flicker = new Random().nextInt(40);
			int r1 = r - flicker;
			int g1 = g - flicker;
			int b1 = b - flicker;
			
			if(r1 < 0) r1 = 0;
			if(g1 < 0) g1 = 0;
			if(b1 < 0) b1 = 0;
			
			pixelHash.put(i, new Color(r1, g1, b1));
		}
		
		OutputManager.addToOutput(PixelColorUtils.pixelHashToColorArray(pixelHash));
		
		int delay = new Random().nextInt(100) + 50;
		super.setDelay(delay);
		
		super.onLoop();
	}

}
