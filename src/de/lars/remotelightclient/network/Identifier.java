package de.lars.remotelightclient.network;

public class Identifier {
	//System
	public final static String SYS_SHUTDOWN_IFNOT_REACHABLE = "SYS_SHUTDOWN_IFNOT_REACHABLE";
	public final static String SYS_SHUTDOWN_CANCEL = "SYS_SHUTDOWN_CANCEL";
	public final static String SYS_SHUTDOWN_NOW = "SYS_SHUTDOWN_NOW";
	
	// Single Colors for brightness control
	public final static String COLOR_RED = "COLOR_RED";
	public final static String COLOR_GREEN = "COLOR_GREEN";
	public final static String COLOR_BLUE = "COLOR_BLUE";
	public final static String COLOR_COLOR = "COLOR_COLOR"; //String lenght: 4 (id,r,g,b)
	public final static String COLOR_LEFT = "COLOR_LEFT"; //String lenght: 4 (id,r,g,b)
	public final static String COLOR_RIGHT = "COLOR_RIGHT"; //String lenght: 4 (id,r,g,b)
	
	//Animations
	public final static String ANI_FLASH = "ANI_FLASH";
	public final static String ANI_JUMP = "ANI_JUMP";
	public final static String ANI_RAINBOW = "ANI_RAINBOW";
	public final static String ANI_PULSE = "ANI_PULSE";
	public final static String ANI_BLINK = "ANI_BLINK";
	public final static String ANI_SET_SPEED = "ANI_SET_SPEED";
	public final static String ANI_SET_STOP = "ANI_SET_STOP";
	
	//Scenes
	public final static String SCENE_STOP = "SCENE_STOP";
	
	//ScreenColor
	public final static String SC_START = "SC_START";
	public final static String SC_STOP = "SC_STOP";
	public final static String SC_COLOR_LEFT = "SC_COLOR_LEFT"; //String lenght: 4 (id,r,g,b)
	public final static String SC_COLOR_RIGHT = "SC_COLOR_RIGHT"; //String lenght: 4 (id,r,g,b)
	
	//WS281x
	public final static String WS_COLOR_ALL = "WS_COLOR_ALL"; //id,r,g,b
	public final static String WS_COLOR_PIXEL = "WS_COLOR_PIXEL"; //id,pixel,r,g,b
	public final static String WS_COLOR_OFF = "WS_COLOR_OFF";
	public final static String WS_COLOR_DIM = "WS_COLOR_DIM"; //id, int dim
	public final static String WS_ANI_STOP = "WS_ANI_STOP";
	public final static String WS_ANI_SPEED = "WS_ANI_STEEP"; //id,speed
	public final static String WS_ANI_RAINBOW = "WS_ANI_RAINBOW";
	public final static String WS_ANI_RUNNING = "WS_ANI_RUNNING";
	public final static String WS_ANI_WIPE = "WS_ANI_WIPE";
	public final static String WS_ANI_SCAN = "WS_ANI_SCAN";
	public final static String WS_ANI_SNAKES = "WS_ANI_SNAKES";
	public final static String WS_SHIFT_LEFT = "WS_SHIFT_LEFT"; //id,pixels to shift
	public final static String WS_SHIFT_RIGHT = "WS_SHIFT_RIGHT"; //if, pixels to shift
	
	//Settings
	public final static String STNG_MODE_RGB = "STNG_MODE_RGB";
	public final static String STNG_MODE_WS28x = "STNG_MODE_WS28x"; //lenght: 2 (id, pixel_num)
	public final static String STNG_BOOT_ANI= "STNG_BOOT_ANI";

}
