package de.lars.remotelightclient;

import java.io.File;
import java.io.IOException;

import org.tinylog.Logger;

import com.blogspot.debukkitsblog.util.FileStorage;

public class DataStorage {
	
	private static FileStorage storage;
	private static final String PSW = "jAW1O4fkWsWC9834rujhGTZUk2FDthPX"; //I know it's pointless
	public static final String SETTINGSMANAGER_KEY = "settingsmanager_key";
	public final static String IP_STOREKEY = "serverip";
	public final static String SOUND_INPUT_STOREKEY = "soundinput";
	public final static String DEVICES_LIST = "devices_list";
	
	public final static String SETTINGS_CONTROL_MODEKEY = "settings_control_mode";
	public final static String SETTINGS_COMPORT = "settings_comport";
	public final static String SETTINGS_COMPORT_AUTOOPEN = "settings_comport_autoopen";
	public final static String SETTINGS_AUTOSTART = "settings_autostart";
	public final static String SETTINGS_HIDE = "settings_hide";
	public final static String SETTINGS_AUTOCONNECT = "settings_autoconnect";
	public final static String SETTINGS_AUTOSHUTDOWN = "settings_autoshutdown";
	public final static String SETTINGS_LED_NUM = "settings_led_num";
	public final static String SETTINGS_BRIGHTNESS = "settings_brightness";
	public final static String SETTINGS_BOOT_ANI = "settings_boot_ani";
	public final static String SETTINGS_BOOT_SHOWLAST = "settings_boot_showlast";
	
	public final static String SETTINGS_SCREENCOLOR_INTERVAL = "settings_screenncolor_interval";
	public final static String SETTINGS_SCREENCOLOR_YPOS = "settings_screenncolor_ypos";
	public final static String SETTINGS_SCREENCOLOR_INVERT = "settings_screenncolor_invert";
	public final static String SETTINGS_SCREENCOLOR_MONITOR = "settings_screencolor_monitor";
	
	public final static String SETTINGS_MUSICSYNC_SENSITIVITY = "settings_musicsync_sensitivity";
	public final static String MUSICSYNC_MODE = "musicsync_mode";
	
	public final static String LEVELBAR_COLOR1 = "levelbar_color1";
	public final static String LEVELBAR_COLOR2 = "levelbar_color2";
	public final static String LEVELBAR_COLOR3 = "levelbar_color3";
	public final static String LEVELBAR_AUTOCHANGE = "levelbar_autochange";
	public final static String LEVELBAR_SMOOTH = "levelbar_smooth";
	
	public final static String RAINBOW_SMOOTH_RISE = "rainbow_smooth_rise";
	public final static String RAINBOW_SMOOTH_FALL = "rainbow_smooth_fall";
	public final static String RAINBOW_STEPS = "rainbow_steps";
	
	public final static String CUSTOM_COLORS_ARRAY = "custom_colors_array";
	
	public static void start() {
		try {
			new File(System.getProperty("user.home") + File.separator  + ".RemoteLightClient").mkdirs();
			storage = new FileStorage(new File(System.getProperty("user.home") + File.separator  + ".RemoteLightClient" + File.separator + "data.dat"));
		} catch (IllegalArgumentException | IOException e) {
			e.printStackTrace();
		}
		if(!isCreated()) System.out.println("ERROR: Could not create data file!");;
	}
	
	public static void store(String key, Object data) {
		try {
			if(isCreated()) {
				if(storage.hasKey(key)) {
					storage.remove(key);
				}
				storage.store(key, data, PSW);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("ERROR: Could not save data!");
		}
	}
	
	public static void remove(String key) {
		try {
			storage.remove(key);
		} catch (IOException e) {
			//do nothing
		}
	}
	
	public static void save() {
		if(isCreated()) {
			try {
				storage.save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Object getData(String key) {
		try {
			if(isCreated())
				return storage.get(key, PSW);
		} catch (Exception e) {
			Logger.error("Could not read data file!");
			e.printStackTrace();
		}
		return null;
	}
	
	public static FileStorage getstorage() {
		return storage;
	}
	
	public static boolean isStored(String key) {
		try {
			Object data = DataStorage.getData(key);
			if(data != null)
				return true;
		} catch (Exception e) {
			//do nothing
		}
		return false;
	}
	
	public static boolean isCreated() {
		if(new File(System.getProperty("user.home") + File.separator + ".RemoteLightClient" + File.separator + "data.dat").isFile())
			return true;
		else
			return false;
	}

}
