package de.lars.remotelightclient.scene.scenes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.network.Client;
import de.lars.remotelightclient.network.Identifier;
import de.lars.remotelightclient.scene.Scene;

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
			Client.send(new String[] {Identifier.WS_SHIFT_RIGHT, 1+""});
			count++;
			if(count >= ocean.length)
				count = 0;
			Client.send(new String[] {Identifier.WS_COLOR_PIXEL, 0+"",
					ocean[count].getRed()+"",
					ocean[count].getGreen()+"",
					ocean[count].getBlue()+""});
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
			
			Client.send(new String[] {Identifier.WS_SHIFT_RIGHT, 1+""});
			
			Client.send(new String[] {Identifier.WS_COLOR_PIXEL, 0+"",
					ocean[count].getRed()+"",
					ocean[count].getGreen()+"",
					ocean[count].getBlue()+""});
			
		} else {
			
			Client.send(new String[] {Identifier.WS_SHIFT_LEFT, 1+""});
			
			Client.send(new String[] {Identifier.WS_COLOR_PIXEL, (pixels - 1)+"",
					ocean[count].getRed()+"",
					ocean[count].getGreen()+"",
					ocean[count].getBlue()+""});
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
