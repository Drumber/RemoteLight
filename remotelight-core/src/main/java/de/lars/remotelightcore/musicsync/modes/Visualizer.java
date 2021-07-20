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

import java.util.Arrays;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.musicsync.MusicEffect;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightcore.utils.color.Color;
import de.lars.remotelightcore.utils.color.ColorUtil;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class Visualizer extends MusicEffect {
	
	private Color[] strip;
	private boolean rainbow = false;
	private SettingBoolean sRainbow;
	private SettingColor sColor;
	private SettingBoolean sSmooth;
	private SettingInt sBlurAmount;
	private SettingSelection sMode;
	private final String[] MODES = {"Legacy", "Smooth", "Mel Filtered"};
	
	private float[] prevData;

	public Visualizer() {
		super("Visualizer");
		sRainbow = this.addSetting(new SettingBoolean("musicsync.visualizer.rainbow", "Rainbow", SettingCategory.MusicEffect, "", false));
		sColor = this.addSetting(new SettingColor("musicsync.visualizer.color", "Color", SettingCategory.MusicEffect, "", Color.RED));
		sMode = this.addSetting(new SettingSelection("musicsync.visualizer.mode", "Mode", SettingCategory.MusicEffect, null, MODES, MODES[1], SettingSelection.Model.ComboBox));
		sBlurAmount = this.addSetting(new SettingInt("musicsync.visualizer.bluramount", "Blur Amount", SettingCategory.MusicEffect, "", 5, 1, 50, 1));
		sSmooth = this.addSetting(new SettingBoolean("musicsync.visualizer.smooth", "Smooth", SettingCategory.MusicEffect, "", false));
	}
	
	@Override
	public void onEnable() {
		strip = new Color[RemoteLightCore.getLedNum()];
		prevData = null;
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		if(rainbow != sRainbow.get()) {
			rainbow = sRainbow.get();
			this.hideSetting(sColor, rainbow);
			this.updateEffectOptions();
		}
		this.hideSetting(sSmooth, sMode.get().equals(MODES[0]));
		
		float[] ampl = getSoundProcessor().getAmplitudes(); //amplitudes
		int[] brightnessData;
		
		if(sMode.get().equals(MODES[0])) {
			brightnessData = getSoundProcessor().computeFFT(ampl, strip.length, 0.5f * getAdjustment());
		} else if(sMode.get().equals(MODES[1])) {
			float[] smoothAmpl = Arrays.copyOfRange(smoothData(ampl), getSoundProcessor().hzToBin(1000), getSoundProcessor().hzToBin(10000));
			// resize array to LED length using linear interpolation
			smoothAmpl = resizeData(smoothAmpl, getLeds());
			brightnessData = new int[smoothAmpl.length];
			for(int i = 0; i < smoothAmpl.length; i++) {
				// map amplitudes to a brightness value between 0 and 255
				int brightness = (int) (smoothAmpl[i] * 1f * getAdjustment());
				brightnessData[i] = Math.min(255, brightness);
			}
		} else {
			float[] melFilter = getSoundProcessor().getMelFilter();
			if(melFilter == null) {
				melFilter = new float[60];
			}
			float[] processedAmpl = resizeData(smoothData(melFilter), getLeds());
			brightnessData = new int[processedAmpl.length];
			for(int i = 0; i < processedAmpl.length; i++) {
				// map amplitudes to a brightness value between 0 and 255
				int brightness = (int) (processedAmpl[i] * 0.05f * getAdjustment());
				brightnessData[i] = Math.min(255, brightness);
			}
		}
		
		for(int i = 0; i < RemoteLightCore.getLedNum(); i++) {
			int brightness = brightnessData[i];
			
			Color c = ColorUtil.dimColor(getColor(i), brightness);
			strip[i] = c;
		}
		
		OutputManager.addToOutput(strip);
		super.onLoop();
	}
	
	private Color getColor(int led) {
		if(rainbow) {
			double mltiplr = (double) RainbowWheel.getRainbow().length / RemoteLightCore.getLedNum();
			return RainbowWheel.getRainbow()[(int) Math.floor(led * mltiplr)];
		} else {
			return sColor.get();
		}
	}

	private float[] smoothData(float[] amplitudes) {
		int smoothValuesAmount = sBlurAmount.get();
		float[] smoothed = new float[amplitudes.length];
		
		for(int i = 0; i < smoothed.length; i++) {
			int startIndex = Math.max(0, i - (smoothValuesAmount - 1));
			int endIndex = Math.min(smoothed.length-1, i + (smoothValuesAmount - 1));
			int currValuesAmount = endIndex - startIndex + 1;
			float sum = 0.0f;
			for(int j = 0; j < currValuesAmount; j++) {
				int elementIndex = startIndex + j;
				sum += amplitudes[elementIndex];
			}
			float avg = sum / currValuesAmount;
			avg = avg * avg;
			// apply filter (reduce low frequencies)
			// this uses an exponential function: -e^(-a*(x+10))+1
			// make 'a' bigger to allow more low frequencies
			float a = -0.01f;
			double volPercent = -Math.exp(a * (i + 10)) + 1;
			smoothed[i] = (float) (volPercent * avg);
		}
		
		// smoothing using simple exponential moving average
		float alpha = 0.5f;
		float alphaRise = 0.95f;
		if(sSmooth.get() && prevData != null && prevData.length == smoothed.length) {
			for(int i = 0; i < smoothed.length; i++) {
				float a = alpha;
				if(smoothed[i] > prevData[i]) {
					a = alphaRise;
				}
				smoothed[i] = prevData[i] + a * (smoothed[i] - prevData[i]);
			}
		}
		
		prevData = smoothed.clone();
		return smoothed;
	}
	
	private float[] resizeData(float[] amplitudes, int newSize) {
		if(amplitudes.length == newSize) {
			return amplitudes;
		}
		float[] out = new float[newSize];
		float fraction = (1.0f * amplitudes.length) / newSize;
		
		for(int i = 0; i < newSize; i++) {
			float position = i * fraction;
			int nextPos = (int) Math.floor(position);
			float difference = position - nextPos;
			
			if(nextPos >= amplitudes.length - 1) {
				out[i] = amplitudes[amplitudes.length - 1];
				continue;
			}
			
			out[i] = (difference * amplitudes[nextPos + 1]) + ((1 - difference) * amplitudes[nextPos]);
		}
		return out;
	}
	
}
