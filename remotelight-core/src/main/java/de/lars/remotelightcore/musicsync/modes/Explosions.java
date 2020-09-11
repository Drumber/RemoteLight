package de.lars.remotelightcore.musicsync.modes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.musicsync.MusicEffect;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.utils.color.ColorUtil;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;
import de.lars.remotelightcore.utils.maths.MathHelper;

public class Explosions extends MusicEffect {
	
	private Color[] strip;
	private List<Explosion> listExplosions;
	private int numExplosions = 5;

	public Explosions() {
		super("Explosions");
	}

	@Override
	public void onEnable() {
		listExplosions = new ArrayList<>();
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum());
		numExplosions = 3;
		
		Random ran = new Random();
		
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
		
		if(listExplosions.size() < numExplosions) {
			Color c = RainbowWheel.getRandomColor();
			int center = ran.nextInt(strip.length);
			float power = ran.nextFloat() + 1.0f;
			listExplosions.add(new Explosion(c, center, power));
		}
		
		OutputManager.addToOutput(strip);
		super.onLoop();
	}
	
	
	private class Explosion {

		Color color;
		int centerPoint;
		float power;
		float radius;
		
		public Explosion(Color color, int centerPoint, float power) {
			this.color = color;
			this.centerPoint = centerPoint;
			this.power = power;
			radius = 0.45f;
		}
		
		
		Color[] draw(Color[] input) {
			calcRadius();
			
			for(int i = 0; i < (int) radius; i++) {
				// distance from radius
				int distance = (int) (radius - i);
				Color c = calcColor(distance);
				
				int posR = centerPoint - 1 + distance;
				int posL = centerPoint - distance;
				if(posR < strip.length)
					input[posR] = c;
				if(posL >= 0)
					input[posL] = c;
			}
			
			// decrease power
			power -= 0.05f;
			power = Math.max(0f, power);
			
			return input;
		}
		
		void calcRadius() {
			// function: f(x) = 2^(-x) + 1
			float speed = (float) Math.pow(2, -radius) + 1;
			radius *= speed;
		}
		
		Color calcColor(int distance) {
			float value = distance;
			float maxValue = (int) radius;
			int brightness = 255 - (int) (MathHelper.map(value, 0f, maxValue, 0f, 255f) * power);
			return ColorUtil.dimColorSimple(color, brightness);
		}
		
	}
	
}
