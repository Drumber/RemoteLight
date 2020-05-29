/*******************************************************************************
 * ______                     _       _     _       _     _   
 * | ___ \                   | |     | |   (_)     | |   | |  
 * | |_/ /___ _ __ ___   ___ | |_ ___| |    _  __ _| |__ | |_ 
 * |    // _ \ '_ ` _ \ / _ \| __/ _ \ |   | |/ _` | '_ \| __|
 * | |\ \  __/ | | | | | (_) | ||  __/ |___| | (_| | | | | |_ 
 * \_| \_\___|_| |_| |_|\___/ \__\___\_____/_|\__, |_| |_|\__|
 *                                             __/ |          
 *                                            |___/           
 * 
 * Copyright (C) 2019 Lars O.
 * 
 * This file is part of RemoteLight.
 ******************************************************************************/
package de.lars.remotelightclient.animation.animations;

import java.awt.Color;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.animation.Animation;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;
import de.lars.remotelightclient.settings.types.SettingInt;
import de.lars.remotelightclient.utils.color.RainbowWheel;

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
		this.numLEDs = Main.getLedNum();
		this.numBalls = ((SettingInt) getSetting("animation.bouncingballs.numballs")).getValue();
		this.h = new float[numBalls];
		this.vImpact0 = (float) Math.sqrt(-2 * GRAVITY * h0);
		this.vImpact = new float[numBalls];
		this.tCycle = new float[numBalls];
		this.pos = new int[numBalls];
		this.tLast = new long[numBalls];
		this.COR = new float[numBalls];
		this.strip = Main.getInstance().getOutputManager().getLastColors();
		
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
