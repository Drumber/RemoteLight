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

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class BouncingBalls extends Animation {
	
	/*
	 * Bouncing Balls by Danny Wilson
	 * https://github.com/daterdots/LEDs/blob/master/BouncingBalls2014/BouncingBalls2014.ino
	 */
	private int numLEDs;
	private final double GRAVITY = -1.5;
	private short h0 = 1;	 // Starting height, in meters, of the ball (strip length)
	private int numBalls;	 // Number of balls
	private float h[];		 // Array of heights
	private float vImpact0;	 // Impact velocity of the ball when it hits the ground if "dropped" from the top of the strip
	private float vImpact[]; // As time goes on the impact velocity will change, so make an array to store those values
	private float tCycle[];	 // The time since the last time the ball struck the ground
	private int pos[];		 // The integer position of the dot on the strip (LED index)
	private long tLast[];	 // The clock time of the last ground strike
	private float COR[];	 // Coefficient of Restitution (bounce damping)
	private Color[] strip;

	public BouncingBalls() {
		super("BouncingBalls");
		this.addSetting(new SettingInt("animation.bouncingballs.numballs", "Number of balls", SettingCategory.Intern, "Changes will be applied next time", 5, 1, 15, 1));
	}
	
	
	@Override
	public void onEnable() {
		this.numLEDs = RemoteLightCore.getLedNum();
		this.numBalls = ((SettingInt) getSetting("animation.bouncingballs.numballs")).get();
		this.h = new float[numBalls];
		this.vImpact0 = (float) Math.sqrt(-2 * GRAVITY * h0);
		this.vImpact = new float[numBalls];
		this.tCycle = new float[numBalls];
		this.pos = new int[numBalls];
		this.tLast = new long[numBalls];
		this.COR = new float[numBalls];
		this.strip = RemoteLightCore.getInstance().getOutputManager().getLastColors();
		
		// SETUP
		for(int i = 0; i < numBalls; i++) {
			tLast[i] = System.currentTimeMillis();
			h[i] = h0;
			pos[i] = 0;				// Balls start on the ground
			vImpact[i] = vImpact0;	// And "pop" up at vImpact0
			tCycle[i] = 0;
			COR[i] = (float) (0.90 - i / Math.pow(numBalls, 2));
		}
		super.onEnable();
	}
	
	
	@Override
	public void onLoop() {
		for(int i = 0; i < numBalls; i++) {
			tCycle[i] = System.currentTimeMillis() - tLast[i];	// Calculate the time since the last time the ball was on the ground
			
			// A little kinematics equation calculates positon as a function of time, acceleration (gravity) and intial velocity
			h[i] = (float) (0.5 * GRAVITY * Math.pow(tCycle[i] / 1000, 2.0) + vImpact[i] * tCycle[i] / 1000);
			
			if(h[i] < 0) {
				h[i] = 0;								// If the ball crossed the threshold of the "ground," put it back on the ground
				vImpact[i] = COR[i] * vImpact[i];		// and recalculate its new upward velocity as it's old velocity * COR
				tLast[i] = System.currentTimeMillis();
				
				if(vImpact[i] < 0.01) {
					vImpact[i] = vImpact0;	// If the ball is barely moving, "pop" it back up at vImpact0
				}
			}
			pos[i] = Math.round(h[i] * (numLEDs - 1) / h0);	// Map "h" to a "pos" integer index position on the LED strip
		}
		
		for(int i = 0; i < numBalls; i++) {
			int color = i * 40;
			if(color >= RainbowWheel.getRainbow().length)
				color = RainbowWheel.getRainbow().length - 1;
			strip[pos[i]] = RainbowWheel.getRainbow()[color];
		}
		OutputManager.addToOutput(strip);
		
		for(int i = 0; i < numBalls; i++) {
			strip[pos[i]] = Color.BLACK;
		}
		super.onLoop();
	}

}
