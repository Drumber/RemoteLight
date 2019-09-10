package de.lars.remotelightclient.scene.scenes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.scene.Scene;
import de.lars.remotelightclient.utils.PixelColorUtils;

public class Ocean extends Scene {
	
	private Color[] ocean;
	private boolean right;
	private int count, loops, pixels;

	public Ocean() {
		super("Ocean", 150);
	}
	
	@Override
	public void onEnable() {
		count = 0; loops = 0; pixels = Main.getLedNum();
		right = true;
		initOcean();
		
		for(int i = 0; i < pixels; i++) {
			PixelColorUtils.shiftRight(1);
			count++;
			if(count >= ocean.length)
				count = 0;
			
			PixelColorUtils.setPixel(0, ocean[count]);
		}
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		count++;
		if(count >= ocean.length) {
			count = 0;
			loops++;
			
			if(loops == 4) {
				if(right) right = false;
				else right = true;
			}
		}
		
		if(right) {
			
			PixelColorUtils.shiftRight(1);
			
			PixelColorUtils.setPixel(0, ocean[count]);
			
		} else {
			
			PixelColorUtils.shiftLeft(1);
			
			PixelColorUtils.setPixel(pixels - 1, ocean[count]);
		}
		super.onLoop();
	}
	
	
	private void initOcean() {
		List<Color> colors = new ArrayList<Color>();
		
		for(int i = 80; i < 256; i += 5)
			colors.add(new Color(0, 0, i));
		
		for(int i = 0; i < 256; i += 5)
			colors.add(new Color(0, i, 255));
		
		for(int i = 255; i > 80; i -= 5)
			colors.add(new Color(0, 255, i));
		
		for(int i = 255; i > 0; i -= 5)
			colors.add(new Color(0, i, 80));
		
		ocean = colors.toArray(new Color[colors.size()]);
	}

}
