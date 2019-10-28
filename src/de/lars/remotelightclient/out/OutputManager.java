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
package de.lars.remotelightclient.out;

import java.awt.Color;

import org.tinylog.Logger;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.devices.ConnectionState;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.types.SettingInt;
import de.lars.remotelightclient.utils.PixelColorUtils;

public class OutputManager {
	
	private SettingsManager sm;
	private volatile Output activeOutput;
	private Color[] outputPixels;
	private Color[] lastPixels;
	private int delay = 50;
	private int brightness = 100;
	private boolean active;
	
	public OutputManager() {
		sm = Main.getInstance().getSettingsManager();
		active = false;
	}

	public Output getActiveOutput() {
		return activeOutput;
	}

	public void setActiveOutput(Output activeOutput) {
		this.setEnabled(false);
		if(this.activeOutput != null && !(this.activeOutput.getId().equals(activeOutput.getId()))) {
			
			deactivate(this.activeOutput);
		}
		if(outputPixels == null) {
			outputPixels = PixelColorUtils.colorAllPixels(Color.BLACK, activeOutput.getPixels());
			lastPixels = PixelColorUtils.colorAllPixels(Color.BLACK, activeOutput.getPixels());
		}

		activate(activeOutput);
		this.activeOutput = activeOutput;
		this.loop();
		
	}
	
	/**
	 * Connects the device if not connected
	 */
	public void activate(Output output) {
		Logger.info("Activate output: " + output.getId());
		if(output.getState() != ConnectionState.CONNECTED) {
			output.onActivate();
		}
	}
	
	/**
	 * Disconnects the device if connected
	 */
	public void deactivate(Output output) {
		Logger.info("Deactivate output: " + output.getId());
		if(output.getState() == ConnectionState.CONNECTED) {
			output.onDeactivate();
		}
	}
	
	/**
	 * 
	 * Sets the delay of the output loop
	 */
	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	/**
	 * 
	 * @return Delay of the output loop
	 */
	public int getDelay() {
		if(((SettingInt) sm.getSettingFromId("out.delay")) != null) {
			delay = ((SettingInt) sm.getSettingFromId("out.delay")).getValue();
		}
		return delay;
	}
	
	/**
	 * 
	 * @param brightness Value between 0 and 100
	 */
	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}
	
	public int getBrightness() {
		return brightness;
	}
	
	/**
	 * 
	 * @return Color array that was last sent
	 */
	public Color[] getLastColors() {
		return lastPixels;
	}
	
	/**
	 * 
	 * Toggle output loop
	 */
	public void setEnabled(boolean enabled) {
		active = enabled;
	}
	
	/**
	 * 
	 * @return True if output loop is running
	 */
	public boolean isEnabled() {
		return active;
	}
	
	public void close() {
		setEnabled(false);
		if(activeOutput != null) {
			activeOutput.onOutput(PixelColorUtils.colorAllPixels(Color.BLACK, Main.getLedNum()));
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
			activeOutput.onOutput(PixelColorUtils.colorAllPixels(Color.BLACK, Main.getLedNum()));
			deactivate(activeOutput);
		}
		//save last output before closing
		sm.getSettingObject("out.lastoutput").setValue(activeOutput);
		//save brightness
		sm.getSettingObject("out.brightness").setValue(getBrightness());
	}
	
	/**
	 * 
	 * @param pixels Color array which length must be equal the number of LEDs
	 */
	public static void addToOutput(Color[] pixels) {
		Main.getInstance().getOutputManager().setOutputPixels(pixels);
	}
	
	private void setOutputPixels(Color[] pixels) {
		lastPixels = pixels;
		outputPixels = this.changeBrightness(pixels, getBrightness());
	}
	
	private Color[] getOutputPixels() {
		Color[] out = outputPixels;
		return out;
	}
	
	private void loop() {
		if(!active) {
			active = true;
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					Logger.info("Started output loop.");
					
					while(active) {
						if((outputPixels != null) && (activeOutput != null)) {
							
							Color[] out = getOutputPixels();
							activeOutput.onOutput(out);
							
							try {
								Thread.sleep(getDelay());
							} catch (InterruptedException e) {
								Logger.error(e);
							}
						}
					}
					Logger.info("Stopped output loop.");
				}
			}, "Output loop").start();
		}
	}
	
	/**
	 * 
	 * @param colors Color array
	 * @param value Dim value between 0 and 100
	 */
	private Color[] changeBrightness(Color[] color, int value) {
		Color[] colors = new Color[color.length];
		if(value < 0) {
			value = 0;
		} else if(value > 100) {
			value = 100;
		}
		for(int i = 0; i < colors.length; i++) {
			int r = color[i].getRed();
			int g = color[i].getGreen();
			int b = color[i].getBlue();
			
			r = r * value / 100;
			g = g * value / 100;
			b = b * value / 100;
			
			colors[i] = new Color(r, g, b);
		}
		return colors;
	}

}
