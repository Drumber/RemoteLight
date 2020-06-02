/*-
 * >===license-start
 * RemoteLight
 * ===
 * Copyright (C) 2019 - 2020 Drumber
 * ===
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * <===license-end
 */

package de.lars.remotelightcore.screencolor;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import org.tinylog.Logger;

import de.lars.remotelightcore.EffectManager;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.EffectManagerHelper.EffectType;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.settings.types.SettingObject;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class ScreenColorManager extends EffectManager {
	
	private boolean active;
	private SettingsManager sm;
	private GraphicsDevice currentMonitor;
	private ScreenColorDetector detector;
	private boolean inverted = false;
	private int delay;
	
	public ScreenColorManager() {
		active = false;
		sm = RemoteLightCore.getInstance().getSettingsManager();
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
	
	@Override
	public String getName() {
		return "ScreenColorManager";
	}
	
	public void start(int yPos, int yHeight, int delay, boolean invert, GraphicsDevice monitor) {
		if(!active) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					RemoteLightCore.getInstance().getEffectManagerHelper().stopAllExceptFor(EffectType.ScreenColor);
					active = true;
					inverted = invert;
					ScreenColorManager.this.delay = delay;
					currentMonitor = monitor;
					//save monitor in settings
					sm.getSettingObject("screencolor.monitor").setValue(monitor.getIDstring());
					Logger.info("Started ScreenColor thread.");
					
					int pixels = RemoteLightCore.getLedNum();
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
					OutputManager.addToOutput(PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum()));
				}
			}).start();
		}
	}
	
	@Override
	public boolean isActive() {
		return active;
	}
	
	@Override
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
