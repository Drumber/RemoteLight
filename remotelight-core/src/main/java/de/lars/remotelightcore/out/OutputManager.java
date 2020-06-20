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

package de.lars.remotelightcore.out;

import java.awt.Color;

import org.tinylog.Logger;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.devices.ConnectionState;
import de.lars.remotelightcore.out.OutputActionListener.OutputActionType;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.utils.OutputUtil;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class OutputManager {
	
	private SettingsManager sm;
	private volatile Output activeOutput;
	private OutputActionListener actionListener;
	private Color[] outputPixels;
	private Color[] lastPixels;
	private int delay = 50;
	private int brightness = 100;
	private boolean active;
	
	public OutputManager() {
		sm = RemoteLightCore.getInstance().getSettingsManager();
		active = false;
	}
	
	public void addOutputActionListener(OutputActionListener l) {
		actionListener = l;
	}
	
	private void fireOutputAction(Output output, OutputActionType type) {
		if(actionListener != null) {
			actionListener.onOutputAction(output, type);
		}
	}

	public synchronized Output getActiveOutput() {
		return activeOutput;
	}

	public synchronized void setActiveOutput(Output activeOutput) {
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
		fireOutputAction(activeOutput, OutputActionType.ACTIVE_OUTPUT_CHANGED);
		this.loop();
	}
	
	/**
	 * Connects the device if not connected
	 */
	public synchronized void activate(Output output) {
		Logger.info("Activate output: " + output.getId() + String.format(" (%s)", OutputUtil.getOutputTypeAsString(output)));
		if(output.getState() != ConnectionState.CONNECTED) {
			output.onActivate();
		}
		fireOutputAction(activeOutput, OutputActionType.ACTIVATED);
	}
	
	/**
	 * Disconnects the device if connected
	 */
	public synchronized void deactivate(Output output) {
		Logger.info("Deactivate output: " + output.getId() + String.format(" (%s)", OutputUtil.getOutputTypeAsString(output)));
		if(output.getState() == ConnectionState.CONNECTED) {
			output.onDeactivate();
		}
		if(activeOutput != null && (output == activeOutput || output.getId().equals(activeOutput.getId()))) {
			activeOutput = null;
		}
		fireOutputAction(activeOutput, OutputActionType.DEACTIVATED);
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
		if(!active && activeOutput != null) {
			deactivate(activeOutput);
		}
			
	}
	
	/**
	 * 
	 * @return True if output loop is running
	 */
	public boolean isEnabled() {
		return active;
	}
	
	public void close() {
		if(activeOutput != null) {
			activeOutput.onOutput(PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum()));
			
			//save last output before closing
			sm.getSettingObject("out.lastoutput").setValue(activeOutput.getId());
		}
		setEnabled(false);
		//save brightness
		sm.getSettingObject("out.brightness").setValue(getBrightness());
	}
	
	/**
	 * 
	 * @param pixels Color array which length must be equal the number of LEDs
	 */
	public static void addToOutput(Color[] pixels) {
		RemoteLightCore.getInstance().getOutputManager().setOutputPixels(pixels);
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
						if((outputPixels != null) && (activeOutput != null) && (activeOutput.getState() == ConnectionState.CONNECTED)) {
							
							Color[] out = getOutputPixels();
							activeOutput.onOutput(out);
							
							try {
								Thread.sleep(getDelay());
							} catch (InterruptedException e) {
								Logger.error(e);
							}
						} else if(activeOutput != null && activeOutput.getState() != ConnectionState.CONNECTED) {
							Logger.info("Output not connected, deactivate Output!");
							setEnabled(false);
							fireOutputAction(activeOutput, OutputActionType.DISCONNECTED);
						} else {
							Logger.info("Invalid output data, disable Output loop!");
							setEnabled(false);
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
