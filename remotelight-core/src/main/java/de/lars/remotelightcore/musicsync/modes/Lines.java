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

import de.lars.remotelightcore.utils.color.Color;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.musicsync.MusicEffect;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightcore.settings.types.SettingSelection.Model;
import de.lars.remotelightcore.utils.color.ColorUtil;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;
import de.lars.remotelightcore.utils.maths.MathHelper;
import de.lars.remotelightcore.utils.maths.TimeUtil;

public class Lines extends MusicEffect {
	
	private String[] colorModes = {"Static", "Rainbow #1", "Rainbow #2", "Spectrum"};
	private String mode;
	private int rainbowAllHue = 0;
	
	private Color[] strip;
	private boolean rotate;
	private TimeUtil rotateTimer;
	private int rotateIndex;
	
	private int linesNum; // number of lines
	private int maxLineLength; // max line length

	public Lines() {
		super("Lines");
		this.addSetting(new SettingBoolean("musicsync.lines.rotate", "Rotate", SettingCategory.MusicEffect, "Rotate strip", false));
		this.addSetting(new SettingSelection("musicsync.lines.colormode", "Color mode", SettingCategory.MusicEffect, null, colorModes, "Static", Model.ComboBox));
		this.addSetting(new SettingColor("musicsync.lines.color", "Color", SettingCategory.MusicEffect, "Static color", Color.RED));
		this.addSetting(new SettingInt("musicsync.lines.lines", "Lines", SettingCategory.MusicEffect, "Number of lines", 10, 2, 300, 1));
	}
	
	@Override
	public void onEnable() {
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum());
		rotateIndex = 0;
		rotateTimer = new TimeUtil(150);
		
		linesNum = strip.length / 10; // number of lines
		maxLineLength = strip.length / linesNum; // maximum line length in pixel
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		handleSettings();
		if(linesNum > strip.length / 2)
			linesNum = strip.length / 2;
		maxLineLength = strip.length / linesNum;
		
		// for color mode Rainbow #1
		if(++rainbowAllHue >= RainbowWheel.getRainbow().length)
			rainbowAllHue = 0;
				
		float[] fft = getSoundProcessor().getAmplitudes();
		int[] data = getSoundProcessor().computeFFT(fft, linesNum, getAdjustment());
		
		for(int i = 0; i < linesNum; i++) {
			// calculate how many led should glows
			int vol = data[i];
			int leds = (int) MathHelper.map(vol, 0, 255, 0, maxLineLength);
						
			int ledIndex = i * maxLineLength; // first led index
			for(int l = ledIndex; l < (ledIndex + maxLineLength); l++) {
				if(l < strip.length) { // some safety check
					
					// rotate option: add rotate index to led position
					int pos = l + rotateIndex;
					if(pos >= strip.length)
						pos -= strip.length;
					
					// set color if smaller than leds (see above)
					if(l < ledIndex + leds) {
						strip[pos] = getColor(l, l - ledIndex + 1);
					} else {
						strip[pos] = Color.BLACK;
					}
				}
			}
		}
		
		// rotate option: increase index
		if(rotate) {
			if(isBump())
				rotateIndex += 2;
			if(rotateTimer.hasReached())
				rotateIndex++;
			if(rotateIndex >= strip.length)
				rotateIndex = 0;
		} else {
			rotateIndex = 0;
		}
		
		OutputManager.addToOutput(strip);
		super.onLoop();
	}
	
	
	private Color getColor(int index, int ledVol) {
		if(mode.equalsIgnoreCase("Rainbow #1")) {
			return RainbowWheel.getRainbow()[rainbowAllHue];
		} else if(mode.equalsIgnoreCase("Rainbow #2")) {
			int mltiplr = RainbowWheel.getRainbow().length / RemoteLightCore.getLedNum();
			return RainbowWheel.getRainbow()[index * mltiplr];
		} else if(mode.equalsIgnoreCase("Spectrum")) {
			int fadeLEDs= maxLineLength - (maxLineLength / 5); // amount of leds which are faded to red
			if(ledVol >= (maxLineLength - fadeLEDs)) {
				float redFade = 1f / fadeLEDs * (fadeLEDs - (maxLineLength - ledVol));
				return ColorUtil.fadeToColor(Color.GREEN, Color.RED, redFade);
			}
			return Color.GREEN;
		}
		// static color
		return getSetting(SettingColor.class, "musicsync.lines.color").get();
	}
	
	private void handleSettings() {
		String prevMode = mode;
		mode = getSetting(SettingSelection.class, "musicsync.lines.colormode").getSelected();
		if(!mode.equals(prevMode)) {
			// hide color option when static mode is not selected
			this.hideSetting("musicsync.lines.color", !mode.equals("Static"));
			this.updateEffectOptions();
		}
		rotate = getSetting(SettingBoolean.class, "musicsync.lines.rotate").get();
		linesNum = getSetting(SettingInt.class, "musicsync.lines.lines").get();
	}

}
