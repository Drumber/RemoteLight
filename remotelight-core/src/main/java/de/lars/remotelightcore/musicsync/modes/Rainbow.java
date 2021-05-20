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

package de.lars.remotelightcore.musicsync.modes;

import de.lars.remotelightcore.musicsync.MusicEffect;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightcore.settings.types.SettingSelection.Model;
import de.lars.remotelightcore.utils.color.Color;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class Rainbow extends MusicEffect {
	
	private int pix;
	private final String[] MODES = {"Centered", "Left", "Right"};
	private String mode = "centered";
	private Color[] strip;
	private boolean isOddNumber;
	private int currentHue = 0;
	private int hueStepSize = 5;
	
	private int lastLeds = 0;
	private boolean smoothRise = true;
	private boolean smoothFall = true;
	
	private final String[] PEAKMARKER_MODES = {"Touched", "Cycle", "Cycle 2"};
	private boolean showPeakMarker = true;
	private String peakMarkerColorMode;
	private float peakMarkerDeltaPos;
	private int peakMarkerHue; // for Cycle and Touched color modes
	private float peakMarkerDropSpeed = 0.2f;

	public Rainbow() {
		super("Rainbow");
		this.addSetting(new SettingSelection("musicsync.rainbow.mode", "Mode", SettingCategory.MusicEffect, "Position of the rainbow bar.", MODES, MODES[0], Model.ComboBox));
		this.addSetting(new SettingBoolean("musicsync.rainbow.smoothrise", "Smooth Rise", SettingCategory.MusicEffect, "", true));
		this.addSetting(new SettingBoolean("musicsync.rainbow.smoothfall", "Smooth Fall", SettingCategory.MusicEffect, "", true));
		this.addSetting(new SettingInt("musicsync.rainbow.steps", "Steps", SettingCategory.MusicEffect, "", 5, 1, 20, 1));
		this.addSetting(new SettingBoolean("musicsync.rainbow.peakmarker", "Peak Marker", SettingCategory.MusicEffect, null, false));
		this.addSetting(new SettingInt("musicsync.rainbow.peakmarker.dropspeed", "Peak Marker Speed", SettingCategory.MusicEffect, "Drop speed of the peak marker.", 2, 1, 10, 1));
		this.addSetting(new SettingSelection("musicsync.rainbow.peakmarker.colormode", "Peak Marker Color Mode", SettingCategory.MusicEffect, null, PEAKMARKER_MODES, PEAKMARKER_MODES[0], Model.ComboBox));
	}
	
	private void initOptions() {
		smoothRise = getSetting(SettingBoolean.class, "musicsync.rainbow.smoothrise").get();
		smoothFall= getSetting(SettingBoolean.class, "musicsync.rainbow.smoothfall").get();
		hueStepSize = getSetting(SettingInt.class, "musicsync.rainbow.steps").get();
		mode = getSetting(SettingSelection.class, "musicsync.rainbow.mode").get().toLowerCase();
		boolean prevShowPeakMarker = showPeakMarker;
		showPeakMarker = getSetting(SettingBoolean.class, "musicsync.rainbow.peakmarker").get();
		peakMarkerDropSpeed = 0.1f * getSetting(SettingInt.class, "musicsync.rainbow.peakmarker.dropspeed").get();
		peakMarkerColorMode = getSetting(SettingSelection.class, "musicsync.rainbow.peakmarker.colormode").get().toLowerCase();
		if(prevShowPeakMarker != showPeakMarker) { // check if peak marker setting has changed
			// hide peak marker related settings if peak marker is disabled
			this.hideSetting("musicsync.rainbow.peakmarker.dropspeed", !showPeakMarker);
			this.hideSetting("musicsync.rainbow.peakmarker.colormode", !showPeakMarker);
			// update effect options panel
			this.updateEffectOptions();
		}
	}
	
	@Override
	public void onEnable() {
		this.initOptions();
		
		pix = getLeds();
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, pix);
		isOddNumber = (pix % 2) != 0;
		peakMarkerDeltaPos = -1;
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		this.initOptions();
		
		// multiplier depends on the gain value and the number of LEDs
		double multiplier = 0.2 * this.getAdjustment() * strip.length / 60;
		int[] amp = getSoundProcessor().getSimpleAmplitudes(); // simple amplitudes: 6 bands
		
		// get the average of all amplitudes
		int x = 0;
		for(int i = 0; i < amp.length; i++) {
			x += amp[i];
		}
		int ampAv = x / amp.length; //average of all amplitudes
		
		int activeLEDs = (int) (ampAv * multiplier); // defines how many LEDs light
		if(activeLEDs > getTotalLEDs()) activeLEDs = getTotalLEDs(); // bound the number of active LEDs
		
		// Smooth fall & rise
		if(smoothRise && smoothFall) {
			if(lastLeds > activeLEDs) {
				activeLEDs = lastLeds;
				lastLeds--;
			} else {
				lastLeds += 2;
				if(lastLeds > activeLEDs) lastLeds = activeLEDs;
				activeLEDs = lastLeds;
			}
		} else if(smoothRise) {
			if(lastLeds > activeLEDs) {
				lastLeds = activeLEDs;
			} else {
				lastLeds += 2;
				if(lastLeds > activeLEDs) lastLeds = activeLEDs;
				activeLEDs = lastLeds;
			}
		} else if(smoothFall) {
			if(lastLeds > activeLEDs) {
				activeLEDs = lastLeds;
				lastLeds--;
			} else {
				lastLeds = activeLEDs;
			}
		}
		
		// set peak marker position
		if(activeLEDs > peakMarkerDeltaPos && activeLEDs != 0) {
			peakMarkerDeltaPos = activeLEDs;
			// set hue of peak marker to hue of active LED
			peakMarkerHue = getPreviousHue(activeLEDs);
		}
		
		for(int i = 0; i < strip.length; i++) {
			int deltaPos = getDistanceToCenter(i);
			Color pixelColor = Color.BLACK;
			// show color if pixel is within activeLEDs range
			if(deltaPos < activeLEDs) {
				int hue = getPreviousHue(deltaPos);
				pixelColor = RainbowWheel.getRainbow()[hue];
			}
			// check if peak marker is at position of the pixel
			if(showPeakMarker && deltaPos == (int) peakMarkerDeltaPos) {
				pixelColor = RainbowWheel.getRainbow()[peakMarkerHue];
			}
			// set color of the pixel
			strip[i] = pixelColor;
		}
		
		// decrement peak marker delta position
		if(peakMarkerDeltaPos > -1.0f) {
			peakMarkerDeltaPos -= peakMarkerDropSpeed;
		}
		
		// increment hue value
		if(isBump()) currentHue += hueStepSize + (hueStepSize / 2) + 20; // bigger step size on loudness peaks
		else currentHue += hueStepSize;
		if((currentHue += hueStepSize) >= RainbowWheel.getRainbow().length)
			currentHue = currentHue % 360;
		
		// increment peak marker hue
		if(peakMarkerColorMode.equals("cycle")) {
			if((peakMarkerHue += hueStepSize / 2) >= RainbowWheel.getRainbow().length)
				peakMarkerHue = peakMarkerHue % 360;
		} else if(peakMarkerColorMode.equals("cycle 2")) {
			peakMarkerHue = currentHue;
		}
		
		OutputManager.addToOutput(strip);
		super.onLoop();
	}
	
	
	/**
	 * Get the effective number of LEDs.
	 * @return	total number of LEDs depending on the mode
	 */
	private int getTotalLEDs() {
		switch(mode) {
		case "centered":
			return (int) (pix / 2.0f + 0.5);
		default: // single bar
			return pix;
		}
	}
	
	/**
	 * Get the distance between the given pixel position
	 * and center position.
	 * @param pixel	pixel position
	 * @return		center pixel position
	 */
	private int getDistanceToCenter(int pixel) {
		switch(mode) {
		case "centered":
			float center = pix / 2.0f;
			if(!isOddNumber) {
				if(pixel <= center - 1) {
					return (int) (center - 1 - pixel);
				}
				return (int) Math.abs(center - pixel);
			} else {
				return Math.abs((int) center - pixel);
			}
		case "right": // right bar
			return pix - 1 - pixel;
		default: // left bar
			return pixel;
		}
	}
	
	/**
	 * Get hue value for a pixel position.
	 * @param deltaPos	distance to the center position
	 * @return
	 */
	private int getPreviousHue(int deltaPos) {
		int targetHue = currentHue - (deltaPos * hueStepSize);
		if(targetHue < 0) targetHue += 360;
		return Math.abs(targetHue % 360);
	}

}
