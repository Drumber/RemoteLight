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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.musicsync.MusicEffect;
import de.lars.remotelightcore.musicsync.MusicSyncManager;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightcore.settings.types.SettingSelection.Model;
import de.lars.remotelightcore.utils.color.ColorUtil;
import de.lars.remotelightcore.utils.color.HSLColor;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;
import de.lars.remotelightcore.utils.maths.MathHelper;

public class Explosions extends MusicEffect {
	
	private SettingColor sColor;
	private Color[] strip;
	private List<Explosion> listExplosions;
	private int maxExplosions;
	private int rainbowStep;

	public Explosions() {
		super("Explosions");
		String[] modes = {"Random", "Rainbow", "Frequency", "Static"};
		this.addSetting(new SettingSelection("musicsync.explosions.mode", "Mode", SettingCategory.MusicEffect, "Explosion color mode", modes, "Random", Model.ComboBox));
		sColor = this.addSetting(new SettingColor("musicsync.explosions.color", "Color", SettingCategory.MusicEffect, null, Color.RED));
	}

	@Override
	public void onEnable() {
		listExplosions = new ArrayList<>();
		maxExplosions = RemoteLightCore.getLedNum() / 5;
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum());
		
		if(this.isBump() && listExplosions.size() < maxExplosions) {
			Random ran = new Random();
			Color c = getColor();
			int center = ran.nextInt(strip.length); // random center point
			
			float multiplier = (float) ((MusicSyncManager.MAX_GAIN + 1 - getAdjustment()) * 0.05f);
			float power = (float) ((getMaxSpl() + 1.0 - getSpl()) * multiplier);
			power = MathHelper.capMinMax(power, 0.05f, 1.5f);
			listExplosions.add(new Explosion(c, center, power));
		}
		
		Iterator<Explosion> iterator = listExplosions.iterator();
		while (iterator.hasNext()) {
			Explosion expl = iterator.next();
			if(expl.power <= 0) {
				// remove powerless explosions
				iterator.remove();
			} else {
				expl.draw(strip);
			}
		}
		
		OutputManager.addToOutput(strip);
		super.onLoop();
	}
	
	private Color getColor() {
		String mode = this.getSetting(SettingSelection.class, "musicsync.explosions.mode").getSelected();
		switch (mode.toLowerCase()) {
		case "random":
			return RainbowWheel.getRandomColor();
		case "rainbow":
			rainbowStep += 10;
			if(rainbowStep >= RainbowWheel.getRainbow().length)
				rainbowStep = 0;
			int hue = Math.min(RainbowWheel.getRainbow().length-1, new Random().nextInt(50) + rainbowStep);
			return RainbowWheel.getRainbow()[hue];
		case "frequency":
			return ColorUtil.soundToColor((int) this.getPitch());
		case "static":
		default:
			return sColor.getValue();
		}
	}
	
	
	private class Explosion {

		Color color;
		int centerPoint;
		float power; // should be between 0.0 and 1.0
		float radius;
		float size = 0.15f; // lower = larger explosion
		
		public Explosion(Color color, int centerPoint, float size) {
			this.color = color;
			this.centerPoint = centerPoint;
			this.size = size;
			this.radius = 0.06f;
			// calc power
			this.power = 0.0f - (size / 5.0f) + 1.1f;
			this.power = MathHelper.capMinMax(power, 0.5f, 1.5f);
		}
		
		
		Color[] draw(Color[] input) {
			calcRadius();
			
			for(int i = 0; i < (int) radius; i++) {
				// distance between radius
				float distance = radius - (1.0f*i);
				// calculate color depending on distance
				Color c = calcColor(distance);
				
				int posR = centerPoint - 1 + (int) distance;
				int posL = centerPoint - (int) distance;
				if(posR < strip.length)
					input[posR] = c;
				if(posL >= 0)
					input[posL] = c;
			}
			
			// decrease power
			power -= 0.015f;
			power = Math.max(0.0f, power);
			
			return input;
		}
		
		void calcRadius() {
			// function: f(x) = 2^(-size * x) + 1
			float speed = (float) Math.pow(2, -size * radius) + 1;
			radius *= speed;
		}
		
		Color calcColor(float distance) {
			// distance = distance away from radius
			HSLColor hsl = new HSLColor(color);
			
			// map distance between 0.0 and 1.0
			float dist = MathHelper.map(distance, 0.0f, radius, 0.0f, 1.0f) * power;
			float brightness = MathHelper.capMinMax(dist, 0.0f, 1.0f);
			return hsl.adjustLuminance(brightness * 100.0f);
		}
		
	}
	
	@Override
	public void onSettingUpdate() {
		String mode = this.getSetting(SettingSelection.class, "musicsync.explosions.mode").getSelected();
		this.hideSetting(sColor, !mode.equals("Static"));
		super.onSettingUpdate();
	}
	
}
