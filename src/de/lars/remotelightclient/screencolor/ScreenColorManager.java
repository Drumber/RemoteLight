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
	
	public ScreenColorManager() {
		active = false;
		sm = Main.getInstance().getSettingsManager();
		sm.addSetting(new SettingObject("screencolor.monitor", "", ""));
		sm.addSetting(new SettingInt("screencolor.delay", "Delay", SettingCategory.Intern, "Delay (ms) between scanning the screen", 150, 25, 2000, 5));
		sm.addSetting(new SettingInt("screencolor.ypos", "Scan height", SettingCategory.Intern, "Y-position at which the pixels are scanned (0 is at the top)", 1000, 0, 2160, 1));
		sm.addSetting(new SettingBoolean("screencolor.invert", "Invert", SettingCategory.Intern, "", false));
		
		String lastMonitor = (String) sm.getSettingObject("screencolor.monitor").getValue();
		if(!lastMonitor.equals("")) {
			currentMonitor = getMonitorByID(lastMonitor);
		}
	}
	
	public void start(int yPos, int delay, boolean invert, GraphicsDevice monitor) {
		if(!active) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					active = true;
					currentMonitor = monitor;
					//save monitor in settings
					sm.getSettingObject("screencolor.monitor").setValue(monitor.getIDstring());
					Logger.info("Started ScreenColor thread.");
					
					int pixels = Main.getLedNum();
					ScreenColorDetector detector = new ScreenColorDetector(pixels, monitor, yPos);
					
					while(active) {
						Color[] c = detector.getColors();
						
						if(c.length <= pixels) {
							Color[] out = new Color[c.length];
							
							if(invert) {
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
							Thread.sleep(delay);
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
