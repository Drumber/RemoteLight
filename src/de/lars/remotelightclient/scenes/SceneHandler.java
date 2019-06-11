package de.lars.remotelightclient.scenes;

import de.lars.remotelightclient.network.Client;
import de.lars.remotelightclient.network.Identifier;

public class SceneHandler {

	public final static int DELAY = 200;
	private static boolean active = false;
	
	public final static String SUNSET = "sunset";
	public final static String FIRE = "fire";
	public final static String NORTHERNLIGHTS = "northernlights";
	public final static String JUNGLE = "jungle";
	public final static String OCEAN = "ocean";
	public final static String SPACE = "space";
	
	public static void stop() {
		active = false;
		Sunset.stop();
		Fire.stop();
		Ocean.stop();
		
		Client.send(new String[] {Identifier.WS_COLOR_OFF});
	}
	
	public static boolean isActive() {
		return active;
	}
	
	public static void start(String scene) {
		stop();
		active = true;
		switch (scene) {
		case SUNSET:
			Sunset.start();
			break;
		case FIRE:
			Fire.start();
			break;
		case NORTHERNLIGHTS:
			
			break;
		case JUNGLE:
			
			break;
		case OCEAN:
			Ocean.start();
			break;
		case SPACE:
			
			break;

		default:
			break;
		}
	}
	
}
