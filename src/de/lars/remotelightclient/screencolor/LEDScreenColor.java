package de.lars.remotelightclient.screencolor;

import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.network.Client;
import de.lars.remotelightclient.network.Identifier;


public class LEDScreenColor {

	private static boolean active;
	private static Timer timer;
	private static Color curentColorL = Color.BLACK;
	private static Color curentColorR = Color.BLACK;
	
	public static void start(int widthL, int heightL, int widthR, int heightR) {
		if(!active) {
			new Thread() {
				
				@Override
				public void run() {
					active = true;
					RgbScreenColorDetector detector = new RgbScreenColorDetector(widthL, heightL, widthR, heightR);
					
					timer = new Timer();
					timer.scheduleAtFixedRate(new TimerTask() {
						
						@Override
						public void run() {
							if(active) {
								curentColorL = detector.getPixelColorLeft();
								curentColorR = detector.getPixelColorRight();
								Client.send(new String[] {Identifier.SC_COLOR_LEFT, curentColorL.getRed()+"", curentColorL.getGreen()+"", curentColorL.getBlue()+""});
								Client.send(new String[] {Identifier.SC_COLOR_RIGHT, curentColorR.getRed()+"", curentColorR.getGreen()+"", curentColorR.getBlue()+""});
								if(Main.getInstance().getRgbGUI() != null)
									Main.getInstance().getRgbGUI().setCurentColorPanel(curentColorL, curentColorR);
							} else
								timer.cancel();
							
						}
					}, 0, 500);
				}
			}.start();
		}
	}
	
	public static void stop() {
		active = false;
		timer.cancel();
	}
	
	public static boolean isActive() {
		return active;
	}
	
	public static Color getColorLeft() {
		return curentColorL;
	}
	
	public static Color getColorRight() {
		return curentColorR;
	}
	
}
