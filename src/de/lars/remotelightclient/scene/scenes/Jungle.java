package de.lars.remotelightclient.scene.scenes;

import java.awt.Color;
import java.util.HashMap;
import java.util.Random;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.network.Client;
import de.lars.remotelightclient.network.Identifier;
import de.lars.remotelightclient.scene.Scene;

public class Jungle extends Scene {
	
	private Random r;
	private int pix, counter, lastGreenNum;
	private HashMap<Integer, Color> backgrnd;
	private HashMap<Integer, Color> points;
	private HashMap<Integer, Integer> pointsFade; //led, fade cycles
	private Color[] colors;

	public Jungle() {
		super("Jungle", 100);
	}
	
	@Override
	public void onEnable() {
		r = new Random();
		pix = Main.getLedNum();
		backgrnd = new HashMap<>();
		Color[] greens = {new Color(0, 255, 0), new Color(150, 200, 0), new Color(110, 190, 5), new Color(0, 100, 5)};
		Color[] colors = {new Color(203, 255, 0), new Color(254, 102, 0), new Color(0, 50, 0), new Color(255, 255, 0)};
		this.colors = colors;
		points = new HashMap<>();
		pointsFade = new HashMap<>();
		/*
		 * SETUP
		 */
		counter = 0; lastGreenNum = 0;
		while(counter != (pix - 1)) {
			int amount = r.nextInt(pix / (pix / 15)) + 3;
			
			if((counter + amount) >= pix)
				amount = (pix - counter) - 1;
			
			int greenNum = 0;
			do {
				greenNum = r.nextInt(greens.length);
			} while(greenNum == lastGreenNum);
			lastGreenNum = greenNum;
			
			for(int i = counter; i <= (counter + amount); i++) {
				backgrnd.put(i, greens[greenNum]);
			}
			
			counter += amount;
		}
		Client.sendWS281xList(backgrnd);
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		if(points.size() < ((pix / 20) + r.nextInt(4))) {
			int led = r.nextInt(pix);
			Color c = colors[r.nextInt(colors.length)];
			
			points.put(led, c);
			pointsFade.put(led, 8);
		}
		
		for(int i = 0; i < pix; i++) {
			if(points.containsKey(i)) {
				//fade
				if(pointsFade.containsKey(i)) {
					Color c = points.get(i);
					int dim = pointsFade.get(i);
					
					int red = c.getRed() / dim;
					int green = c.getGreen() / dim;
					int blue = c.getBlue() / dim;
					
					Client.send(new String[] {Identifier.WS_COLOR_PIXEL, i+"", red+"", green+"", blue+""});
					
					dim--;
					
					if(dim < 1) {
						pointsFade.remove(i);
					} else {
						pointsFade.put(i, dim);
					}
					
				} else {
					
					Color c = points.get(i);
					Client.send(new String[] {Identifier.WS_COLOR_PIXEL, i+"", c.getRed()+"", c.getGreen()+"", c.getBlue()+""});
					
					int red = c.getRed() - (c.getRed() / 6);
					int green = c.getGreen() - (c.getGreen() / 6);
					int blue = c.getBlue() - (c.getBlue() / 6);
					
					if(red < 10 && green < 10 && blue < 10) {
						Color bg = backgrnd.get(i);
						Client.send(new String[] {Identifier.WS_COLOR_PIXEL, i+"", bg.getRed()+"", bg.getGreen()+"", bg.getBlue()+""});
						points.remove(i);
						
					} else {
						points.put(i, new Color(red, green, blue));
					}
				}
			}
		}
		super.onLoop();
	}

}
