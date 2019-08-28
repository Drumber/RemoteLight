package de.lars.remotelightclient.scene.scenes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.network.Client;
import de.lars.remotelightclient.network.Identifier;
import de.lars.remotelightclient.scene.Scene;

public class Sunset extends Scene {
	
	private Color[] sun;
	private int count;

	public Sunset() {
		super("Sunset", 150);
	}
	
	@Override
	public void onEnable() {
		count = 0;
		initSun();
		
		for(int i = 0; i < Main.getLedNum(); i++) {
			Client.send(new String[] {Identifier.WS_SHIFT_RIGHT, 1+""});
			count++;
			if(count >= sun.length)
				count = 0;
			Client.send(new String[] {Identifier.WS_COLOR_PIXEL, 0+"", sun[count].getRed()+"", sun[count].getGreen()+"", sun[count].getBlue()+""});
		}
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		Client.send(new String[] {Identifier.WS_SHIFT_RIGHT, 1+""});
		
		count++;
		if(count >= sun.length)
			count = 0;
		
		Client.send(new String[] {Identifier.WS_COLOR_PIXEL, 0+"", sun[count].getRed()+"", sun[count].getGreen()+"", sun[count].getBlue()+""});
		
		super.onLoop();
	}
	
	
	private void initSun() {
		List<Color> colors = new ArrayList<Color>();
		
		for(int i = 0; i < 106; i += 5)
			colors.add(new Color(150 + i, 0, 0));
		
		colors.add(new Color(255, 0, 0));
		for(int i = 0; i < 160; i++)
			colors.add(new Color(255, i, 0));
	
		for(int i = 160; i > 0; i--)
			colors.add(new Color(255, i, 0));
		colors.add(new Color(255, 0, 0));
		
		for(int i = 0; i < 106; i += 5)
			colors.add(new Color(255 - i, 0, 0));
		
		sun = colors.toArray(new Color[colors.size()]);
	}

}
