package de.lars.remotelightclient.scene.scenes;

import java.util.HashMap;
import java.util.Random;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.network.Client;
import de.lars.remotelightclient.network.Identifier;
import de.lars.remotelightclient.scene.Scene;

public class Space extends Scene {
	
	private int pix;
	private HashMap<Integer, Integer> stars;  //ledNum, Brightness
	private Random r;

	public Space() {
		super("Space", 50);
	}
	
	@Override
	public void onEnable() {
		pix = Main.getLedNum();
		stars = new HashMap<>();
		r = new Random();
		Client.send(new String[] {Identifier.WS_COLOR_OFF});
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		if(stars.size() < ((pix / 4) + r.nextInt(10))) {
			int led = r.nextInt(pix);
			int brightness = r.nextInt(200) + 56;
			
			stars.put(led, brightness);
		}
		
		for(int i = 0; i < pix; i++) {
			if(stars.containsKey(i)) {
				
				int b = stars.get(i);
				Client.send(new String[] {Identifier.WS_COLOR_PIXEL, i+"", b+"", b+"", (b / 2)+""});
				
				b -= 5;
				if(b <= 0) {
					Client.send(new String[] {Identifier.WS_COLOR_PIXEL, i+"", 0+"", 0+"", 0+""});
					stars.remove(i);
				} else {
					stars.put(i, b);
				}
			}
		}
		super.onLoop();
	}

}
