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

package de.lars.remotelightcore.animation.animations;

import de.lars.remotelightcore.utils.color.Color;
import java.util.Random;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightcore.settings.types.SettingSelection.Model;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class Rainbow extends Animation {
	
	private final String[] modes = {"Simple - Cycle", "Simple - Static", "Natural - Sinewave", "Natural - Sinebow"};
	private SettingSelection settingMode;
	private SettingInt settingResolution;
	
	private Color[] strip;
	private float step;
	private float increment = 0.001f;

	public Rainbow() {
		super("Rainbow");
		settingMode = this.addSetting(new SettingSelection("animation.rainbow.mode", "Mode", SettingCategory.Intern, "Rainbow mode", modes, modes[0], Model.ComboBox));
		settingResolution = this.addSetting(new SettingInt("animation.rainbow.resolution", "Resolution", SettingCategory.Intern, "Rainbow resolution", 10, 1, 200, 5));
	}
	
	@Override
	public void onEnable() {
		step = new Random().nextFloat(); // random start point
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum());
		// fill strip
		for(int i = 0; i < strip.length; i++)
			executeSelectedMode(); 
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		increment = settingResolution.get() * 0.001f;
		executeSelectedMode();
		OutputManager.addToOutput(strip);
		super.onLoop();
	}
	
	private void executeSelectedMode() {
		int modeIndx = settingMode.getSelectedIndex();
		switch (modeIndx) {
		case 0:
			hsvRainbow();
			break;
		case 1:
			hsvNoCycleRainbow();
			break;
		case 2:
			sineWaveRainbow();
			break;
		case 3:
			sinebowRainbow();
			break;
		default:
			hsvRainbow();
			break;
		}
	}
	
	private void hsvRainbow() {
		for(int i = strip.length-1; i > 0; i--) {
			strip[i] = strip[i-1];
		}
		if((step += increment) > 1.0f) {
			step = 0.0f;
		}
		strip[0] = Color.getHSBColor(step, 1.0f, 1.0f);
	}
	
	private void hsvNoCycleRainbow() {
		Color color = Color.getHSBColor(step, 1.0f, 1.0f);
		for(int i = 0; i < strip.length; i++) {
			strip[i] = color;
		}
		if((step += increment) > 1.0f) {
			step = 0.0f;
		}
	}
	
	private void sineWaveRainbow() {
		float frequency = increment * 10.0f;
		for(int i = 0; i < strip.length; i++) {
			int red	  = (int) (Math.sin(frequency * i + 0 - step) * 127 + 128);
			int green = (int) (Math.sin(frequency * i + 2 - step) * 127 + 128);
			int blue  = (int) (Math.sin(frequency * i + 4 - step) * 127 + 128);
			strip[i] = new Color(red, green, blue);
		}
		if((step += 0.1f) > (Float.MAX_VALUE - 2*increment)) {
			step = 0.0f;
		}
	}
	
	private void sinebowRainbow() {
		for(int i = 0; i < strip.length; i++) {
			double res = 2.0 + increment;
			double r = (Math.sin(res * Math.PI * (i +       0) - step) * 0.5 + 0.5);
			double g = (Math.sin(res * Math.PI * (i + 1.0/3.0) - step) * 0.5 + 0.5);
			double b = (Math.sin(res * Math.PI * (i + 2.0/3.0) - step) * 0.5 + 0.5);
			
			int red   = (int) (r * 255.0);
			int green = (int) (g * 255.0);
			int blue  = (int) (b * 255.0);
			strip[i] = new Color(red, green, blue);
		}
		if((step += 0.1f) > (Float.MAX_VALUE - 1.0f)) {
			step = 0.0f;
		}
	}

}
