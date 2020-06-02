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

import java.awt.Color;
import java.util.Random;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class Scanner extends Animation {
	
	private Color[] colors;
	private int pos;
	private boolean reverse;
	private Color color, color2;
	private Color[] strip;
	private String lastMode;

	public Scanner() {
		super("Scanner");
//		Main.getInstance().getSettingsManager().removeSetting("animation.scanner.mode");
		this.addSetting(new SettingBoolean("animation.scanner.randomcolor", "Random color", SettingCategory.Intern, null, true));
		this.addSetting(new SettingColor("animation.scanner.color", "Color", SettingCategory.Intern,	null, Color.RED));
		this.addSetting(new SettingSelection("animation.scanner.mode", "Mode", SettingCategory.Intern, "",
				new String[] {"Single", "Dual", "Knight Rider"}, "Single", SettingSelection.Model.ComboBox));
		lastMode = ((SettingSelection) this.getSetting("animation.scanner.mode")).getSelected();
	}
	
	@Override
	public void onEnable() {
		colors = new Color[] {Color.RED, Color.ORANGE, Color.YELLOW, Color.PINK, Color.MAGENTA,
	    		Color.BLUE, Color.CYAN, Color.GREEN};
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum());
		pos = 0;
		color = getRandomColor();
		color2 = getRandomColor();
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		String mode = ((SettingSelection) this.getSetting("animation.scanner.mode")).getSelected();
		if(!mode.equals(lastMode)) {
			PixelColorUtils.setAllPixelsBlack();
			strip = PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum());
			lastMode = mode;
			pos = 0;
		}
		if(mode.equals("Single")) {
			single();
		} else if(mode.equals("Dual")) {
			dual();
		} else if(mode.equals("Knight Rider")) {
			knightRider();
		}
		super.onLoop();
	}
	
	private void single() {
		if(!reverse) {
			if(++pos >= RemoteLightCore.getLedNum() - 1) {
				reverse = true;
			}
		} else {
			if(--pos <= 0) {
				pos = 0;
				reverse = false;
				color = getRandomColor();
			}
		}
		if(!((SettingBoolean) getSetting("animation.scanner.randomcolor")).getValue()) {
			color = ((SettingColor) getSetting("animation.scanner.color")).getValue();
		}
		strip[pos] = color;
		OutputManager.addToOutput(strip);
		strip[pos] = Color.BLACK;
	}
	
	
	private void dual() {
		if(!reverse) {
			if(++pos >= RemoteLightCore.getLedNum() - 1) {
				reverse = true;
			}
		} else {
			if(--pos <= 0) {
				pos = 0;
				reverse = false;
				color = getRandomColor();
				color2 = getRandomColor();
			}
		}
		if(!((SettingBoolean) getSetting("animation.scanner.randomcolor")).getValue()) {
			color = ((SettingColor) getSetting("animation.scanner.color")).getValue();
			color2 = color;
		}
		strip[pos] = color;
		strip[strip.length - 1 - pos] = color2;
		OutputManager.addToOutput(strip);
		strip[pos] = Color.BLACK;
		strip[strip.length - 1 - pos] = Color.BLACK;
	}
	
	
	private void knightRider() {
		Color c = new Color(255, 10, 0);
		if(!reverse) {
			if(++pos >= RemoteLightCore.getLedNum() - 1) {
				reverse = true;
			}
		} else {
			if(--pos <= 0) {
				pos = 0;
				reverse = false;
			}
		}
		strip[pos] = c;
		OutputManager.addToOutput(strip);
		dimAll(strip);
	}
	
	private void dimAll(Color[] pixels) {
		for(int i = 0; i < pixels.length; i++) {
			int r = pixels[i].getRed() - 40;
			int g = pixels[i].getGreen() - 40;
			int b = pixels[i].getBlue() - 40;
			
			if(r < 0) r = 0;
			if(g < 0) g = 0;
			if(b < 0) b = 0;
			
			pixels[i] = new Color(r, g, b);
		}
	}
	
	private Color getRandomColor() {
		int r = new Random().nextInt(colors.length);
		return colors[r];
	}

}
