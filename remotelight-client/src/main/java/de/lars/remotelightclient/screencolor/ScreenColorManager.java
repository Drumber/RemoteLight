/*-
 * >===license-start
 * RemoteLight
 * ===
 * Copyright (C) 2019 - 2020 Lars O.
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

package de.lars.remotelightclient.screencolor;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import org.tinylog.Logger;

import de.lars.remotelightcore.EffectManagerHelper.EffectType;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.screencolor.AbstractScreenColorManager;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingDouble;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.settings.types.SettingObject;
import de.lars.remotelightcore.utils.color.Color;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class ScreenColorManager extends AbstractScreenColorManager {
	
	private boolean active;
	private SettingsManager sm;
	private GraphicsDevice currentMonitor;
	private ScreenColorDetector detector;
	private final Rectangle area;
	private boolean inverted = false;
	private int delay;
	protected float brightnessThreshold;
	protected float saturationMultiplier;
	
	public ScreenColorManager() {
		active = false;
		sm = RemoteLightCore.getInstance().getSettingsManager();
		sm.addSetting(new SettingObject("screencolor.monitor", "", ""));
		sm.addSetting(new SettingInt("screencolor.delay", "Delay", SettingCategory.Intern, "Delay (ms) between scanning the screen", 150, 25, 2000, 5));
		sm.addSetting(new SettingBoolean("screencolor.invert", "Invert", SettingCategory.Intern, "", false));
		
		SettingInt settingX = sm.addSetting(new SettingInt("screencolor.area.x", "Scan Area X", SettingCategory.Intern, "X-position of the scan area", 0, 0, Integer.MAX_VALUE, 5));
		SettingInt settingY = sm.addSetting(new SettingInt("screencolor.area.y", "Scan Area Y", SettingCategory.Intern, "Y-position of the scan area", 500, 0, Integer.MAX_VALUE, 5));
		SettingInt settingW = sm.addSetting(new SettingInt("screencolor.area.width", "Scan Area Width", SettingCategory.Intern, "Width of the scan area", 500, 10, Integer.MAX_VALUE, 5));
		SettingInt settingH = sm.addSetting(new SettingInt("screencolor.area.height", "Scan Area Height", SettingCategory.Intern, "Height of the scan area", 50, 5, Integer.MAX_VALUE, 5));
		
		SettingInt settingBrghtThreshold = sm.addSetting(new SettingInt("screencolor.filter.brightness.threshold", "Brightness Threshold", SettingCategory.Intern, "Show black if brightness of pixel is below threshold", 0, 0, 100, 5));
		SettingDouble settingSaturationMltplr = sm.addSetting(new SettingDouble("screencolor.filter.saturation.multiplier", "Saturation Multiplier", SettingCategory.Intern, "Multiply the saturation of every color (default 1)", 1.0, 0.01, 2.0, 0.05));
		setSaturationMultiplier(settingSaturationMltplr.get());
		setBrightnessThreshold(settingBrghtThreshold.get());
		
		area = new Rectangle();
		setScanArea(settingX.get(), settingY.get(), settingW.get(), settingH.get());
		
		String lastMonitor = (String) sm.getSettingObject("screencolor.monitor").get();
		if(lastMonitor != null && !lastMonitor.equals("")) {
			currentMonitor = getMonitorByID(lastMonitor);
		}
	}
	
	public void start() {
		if(!active && currentMonitor != null) {
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					RemoteLightCore.getInstance().getEffectManagerHelper().stopAllExceptFor(EffectType.ScreenColor);
					active = true;
					//save monitor in settings
					sm.getSettingObject("screencolor.monitor").setValue(currentMonitor.getIDstring());
					Logger.info("Started ScreenColor thread.");
					
					int pixels = RemoteLightCore.getLedNum();
					detector = new ScreenColorDetector(pixels, currentMonitor, area);
					
					detector.brightnessThreshold = brightnessThreshold;
					detector.saturationMultiplier = saturationMultiplier;
					
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
							Logger.error("[ScreenColor] There are more color measuring points than LEDs! Disabling ScreenColor...");
							stop();
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
	
	/**
	 * Set the bounds of the scan area.
	 * @param x			x position
	 * @param y			y position
	 * @param width		area width
	 * @param height	area height
	 */
	public void setScanArea(int x, int y, int width, int height) {
		area.setBounds(x, y, width, height);
	}
	
	/**
	 * Get the scan area.
	 * @return			scan area as rectangle
	 */
	public Rectangle getScanArea() {
		return area;
	}
	
	public void setInverted(boolean invert) {
		this.inverted = invert;
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
	
	/**
	 * Set brightness threshold value
	 * @param threshold		value between 0 and 100
	 */
	public void setBrightnessThreshold(int threshold) {
		brightnessThreshold = threshold / 100.0f;
		if(detector != null)
			detector.brightnessThreshold = brightnessThreshold;
	}
	
	public void setSaturationMultiplier(double multiplier) {
		saturationMultiplier = (float) multiplier;
		if(detector != null)
			detector.saturationMultiplier = saturationMultiplier;
	}

}
