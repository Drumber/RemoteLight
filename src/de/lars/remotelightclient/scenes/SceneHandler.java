package de.lars.remotelightclient.scenes;

public class SceneHandler {

	public final static int DELAY = 100;
	private static boolean active = false;
	
	public final static String SUNSET = "sunset";
	public final static String FIRE = "fire";
	public final static String NORTHERNLIGHTS = "northernlights";
	public final static String JUNGLE = "jungle";
	public final static String SEA = "sea";
	public final static String SPACE = "space";
	
	public static void stop() {
		active = false;
		Sunset.stop();
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
			
			break;
		case NORTHERNLIGHTS:
			
			break;
		case JUNGLE:
			
			break;
		case SEA:
			
			break;
		case SPACE:
			
			break;

		default:
			break;
		}
	}
	
}
