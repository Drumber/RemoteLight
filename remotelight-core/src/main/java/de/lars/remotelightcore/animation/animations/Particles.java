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

import java.util.Random;

import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.utils.color.Color;
import de.lars.remotelightcore.utils.color.ColorUtil;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;
import de.lars.remotelightcore.utils.maths.MathHelper;
import de.lars.remotelightcore.utils.maths.OpenSimplexNoise;

public class Particles extends Animation {
	
	private Color[] strip;
	private Particle[] particles;

	public Particles() {
		super("Particles");
		this.addSetting(new SettingBoolean("animation.particles.staticcolor", "Static color", SettingCategory.Intern, null, false));
		this.addSetting(new SettingColor("animation.particles.color", "Color", SettingCategory.Intern, null, Color.RED));
	}
	
	@Override
	public void onEnable(int pixels) {
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, pixels);
		
		int amount = strip.length / 4;
		particles = new Particle[amount];
		
		for(int i = 0; i < amount; i++) {
			particles[i] = new Particle(-10, strip.length+10);
		}
		
		super.onEnable(pixels);
	}
	
	@Override
	public Color[] onEffect() {
		// clear strip
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, getPixel());
		
		for(int i = 0; i < particles.length; i++) {
			Particle p = particles[i];
			
			p.increment(0.01, 0.001, 0.01);
			
			int pos = p.getPos();
			Color c = p.getColor();
			
			if(pos >= 0 && pos < strip.length) {
				strip[pos] = c;
			}
		}
		
		return strip;
	}
	
	@Override
	public void onSettingUpdate() {
		this.hideSetting("animation.particles.color", !getSetting(SettingBoolean.class, "animation.particles.staticcolor").get());
		super.onSettingUpdate();
	}
	
	
	private class Particle {
		
		OpenSimplexNoise posNoise;
		OpenSimplexNoise hueNoise;
		OpenSimplexNoise brightNoise;
		
		int min, max;
		double posX, posY;
		double hueX, hueY;
		double brightX, brightY;
		
		public Particle(int min, int max) {
			this.min = min;
			this.max = max;
			posNoise = new OpenSimplexNoise();
			hueNoise = new OpenSimplexNoise();
			brightNoise = new OpenSimplexNoise(new Random().nextLong());
			posX = Math.random() * 100f;
			posY = Math.random() * 100f;
			hueX = Math.random();
			hueY = Math.random();
			brightX = Math.random() * 100f;
			brightY = Math.random() * 100f;
		}
		
		public void increment(double posIncrement, double hueIncrement, double brightIncrement) {
			posX += posIncrement;
			posY += posIncrement;
			hueX += hueIncrement;
			hueY += hueIncrement;
			brightX += brightIncrement;
			brightY += brightIncrement;
		}
		
		public int getPos() {
			float pos = (float) posNoise.eval(posX, posY);
			return (int) MathHelper.map(pos, -1, 1, min, max);
		}
		
		public Color getColor() {
			float hueVal = (float) hueNoise.eval(hueX, hueY);
			int hue = (int) MathHelper.map(hueVal, -1, 1, 0, RainbowWheel.getRainbow().length-1);
			Color color = RainbowWheel.getRainbow()[hue];
			
			if(((SettingBoolean)getSetting("animation.particles.staticcolor")).get()) {
				color = ((SettingColor) getSetting("animation.particles.color")).get();
			}
			
			float brightVal = (float) brightNoise.eval(brightX, brightY);
			int bright = (int) MathHelper.map(brightVal, -1, 1, -25, 100);
			bright = Math.max(0, bright);
			return ColorUtil.dimColor(color, bright);
		}
		
	}

}
