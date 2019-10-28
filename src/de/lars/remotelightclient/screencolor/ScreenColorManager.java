/*******************************************************************************
 * ______                     _       _     _       _     _   
 * | ___ \                   | |     | |   (_)     | |   | |  
 * | |_/ /___ _ __ ___   ___ | |_ ___| |    _  __ _| |__ | |_ 
 * |    // _ \ '_ ` _ \ / _ \| __/ _ \ |   | |/ _` | '_ \| __|
 * | |\ \  __/ | | | | | (_) | ||  __/ |___| | (_| | | | | |_ 
 * \_| \_\___|_| |_| |_|\___/ \__\___\_____/_|\__, |_| |_|\__|
 *                                             __/ |          
 *                                            |___/           
 * 
 * Copyright (C) 2019 Lars O.
 * 
 * This file is part of RemoteLight.
 ******************************************************************************/
package de.lars.remotelightclient.screencolor;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import org.tinylog.Logger;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;
import de.lars.remotelightclient.settings.types.SettingBoolean;
import de.lars.remotelightclient.settings.types.SettingInt;
import de.lars.remotelightclient.settings.types.SettingObject;
import de.lars.remotelightclient.utils.PixelColorUtils;

public class ScreenColorManager {
	
	private boolean active;
	private SettingsManager sm;
	private GraphicsDevice currentMonitor;
	private ScreenColorDetector detector;
	private boolean inverted = false;
	private int delay;
	
	public ScreenColorManager() {
		active = false;
		sm = Main.getInstance().getSettingsManager();
		sm.addSetting(new SettingObject("screencolor.monitor", "", ""));
		sm.addSetting(new SettingInt("screencolor.delay", "Delay", SettingCategory.Intern, "Delay (ms) between scanning the screen", 150, 25, 2000, 5));
		sm.addSetting(new SettingInt("screencolor.ypos", "Scan Y-position", SettingCategory.Intern, "Y-position at which the pixels are scanned (0 is at the top)", 500, 0, 2160, 5));
		sm.addSetting(new SettingInt("screencolor.yheight", "Area height", SettingCategory.Intern, "The height of the scan area", 20, 0, 200, 5));
		sm.addSetting(new SettingBoolean("screencolor.invert", "Invert", SettingCategory.Intern, "", false));
		
		String lastMonitor = (String) sm.getSettingObject("screencolor.monitor").getValue();
		if(!lastMonitor.equals("")) {
			currentMonitor = getMonitorByID(lastMonitor);
		}
	}
	
	public void start(int yPos, int yHeight, int delay, boolean invert, GraphicsDevice monitor) {
		if(!active) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					active = true;
					inverted = invert;
					ScreenColorManager.this.delay = delay;
					currentMonitor = monitor;
					//save monitor in settings
					sm.getSettingObject("screencolor.monitor").setValue(monitor.getIDstring());
					Logger.info("Started ScreenColor thread.");
					
					int pixels = Main.getLedNum();
					detector = new ScreenColorDetector(pixels, monitor, yPos, yHeight);
					
					while(active) {
						Color[] c = detector.getColors();
						
						if(c.length <= pixels) {
							Color[] out = new Color[c.length];
							
							if(inverted) {
								for(int i = 0; i < c.length; i++) {
									out[i] = c[c.length - 1 - i];
								}
							} else {
								out = c;
							}
							OutputManager.addToOutput(out);
							
						} else {
							Logger.error("[ScreenColor] There are more color measuring points than LEDs!");
						}
						
						try {
							Thread.sleep(ScreenColorManager.this.delay);
						} catch (InterruptedException e) {
							Logger.error(e);
						}
					}
					
					Logger.info("Stopped ScreenColor thread.");
					OutputManager.addToOutput(PixelColorUtils.colorAllPixels(Color.BLACK, Main.getLedNum()));
				}
			}).start();
		}
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void stop() {
		if(active) {
			active = false;
		}
	}
	
	public GraphicsDevice getCurrentMonitor() {
		return currentMonitor;
	}
	
	public void setInverted(boolean invert) {
		this.inverted = invert;
	}
	
	public void setYPos(int ypos) {
		if(detector != null) {
			detector.setYPos(ypos);
		}
	}
	
	public void setYHeight(int yHeight) {
		if(detector != null) {
			detector.setYHeight(yHeight);
		}
	}
	
	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	public void setMonitor(GraphicsDevice monitor) {
		sm.getSettingObject("screencolor.monitor").setValue(monitor.getIDstring());
		currentMonitor = monitor;
		if(detector != null) {
			detector.setMonitor(monitor);
		}
	}
	
	public static GraphicsDevice[] getMonitors() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
	}
	
	public static GraphicsDevice getMonitorByID(String id) {
		for(GraphicsDevice gd : getMonitors()) {
			if(gd.getIDstring().equals(id)) {
				return gd;
			}
		}
		return null;
	}

}
