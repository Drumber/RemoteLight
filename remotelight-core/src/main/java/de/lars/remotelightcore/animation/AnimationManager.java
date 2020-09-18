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

package de.lars.remotelightcore.animation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.tinylog.Logger;

import de.lars.remotelightcore.EffectManager;
import de.lars.remotelightcore.EffectManagerHelper.EffectType;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.animation.animations.*;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.Setting;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class AnimationManager extends EffectManager {
	
	private Animation activeAnimation;
	private List<Animation> animations;
	private boolean active;
	private int delay = 50;
	
	public AnimationManager() {
		animations = new ArrayList<Animation>();
		this.registerAnimations();
	}
	
	@Override
	public String getName() {
		return "AnimationManager";
	}
	
	@Override
	public boolean isActive() {
		return (activeAnimation != null);
	}
	
	public Animation getActiveAnimation() {
		return activeAnimation;
	}
	
	public void addAnimation(Animation animation) {
		animations.add(animation);
	}
	
	public void removeAnimation(Animation animation) {
		animations.remove(animation);
	}
	
	public List<Animation> getAnimations() {
		return animations;
	}
	
	public List<Setting> getCurrentAnimationOptions() {
		SettingsManager sm = RemoteLightCore.getInstance().getSettingsManager();
		List<Setting> tmp = new ArrayList<>();
		if(getActiveAnimation() == null) {
			return tmp;
		}
		for(String s : getActiveAnimation().getOptions()) {
			tmp.add(sm.getSettingFromId(s));
		}
		return tmp;
	}
	
	public void onEffectOptionChanged() {
		if(activeAnimation != null)
			activeAnimation.onSettingUpdate();
	}
	
	public void start(Animation animation) {
		RemoteLightCore.getInstance().getEffectManagerHelper().stopAllExceptFor(EffectType.Animation);
		if(activeAnimation != null) {
			activeAnimation.onDisable();
		}
		OutputManager.addToOutput(PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum()));
		if(animation != null) {
			animation.onEnable();
		}
		activeAnimation = animation;
		this.loop();
	}
	
	@Override
	public void stop() {
		if(activeAnimation != null) {
			activeAnimation.onDisable();
			activeAnimation = null;
		}
		OutputManager.addToOutput(PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum()));
	}
	
	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	public int getDelay() {
		return this.delay;
	}
	
	private void loop() {
		if(!active) {
			active = true;
			Logger.info("Starting Animation Thread.");
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					while(activeAnimation != null) {
						try {
							
							activeAnimation.onLoop();
							
						} catch(Exception e) {
							Logger.error(e, "There was an error executing the animation '" + activeAnimation.getDisplayname() + "'.");
							// stop on exception
							stop();
							break;
						}
						
						if(activeAnimation.isAdjustable()) {
							try {
								Thread.sleep(delay);
							} catch (InterruptedException e) {
								Logger.error(e, "Animation Thread could not wait for delay! (delay: " + delay + ")");
							}
						} else {
							try {
								Thread.sleep(activeAnimation.getDelay());
							} catch (InterruptedException e) {
								Logger.error(e, "Animation Thread could not wait for delay! (delay: " + activeAnimation.getDelay() + ")");
							}
						}
					}
					active = false;
					Logger.info("Stopped Animation Thread.");
				}
			}).start();
		}
	}
	
	private void registerAnimations() {
		animations.add(new Rainbow());
		animations.add(new RunningLight());
		animations.add(new Scanner());
		animations.add(new Wipe());
		animations.add(new TheaterChase());
		animations.add(new Shake());
		animations.add(new Twinkle());
		animations.add(new SoftTwinkles());
		animations.add(new Jump());
		animations.add(new Fade());
		animations.add(new Meteor());
		animations.add(new RandomColor());
		animations.add(new Close());
		animations.add(new Open());
		animations.add(new Breath());
		animations.add(new TwoColors());
		animations.add(new BouncingBalls());
		animations.add(new ColorWave());
		animations.add(new Snake());
		animations.add(new Confetti());
		animations.add(new Sinelon());
		animations.add(new Juggle());
		animations.add(new Spread());
		animations.add(new Muddle());
		animations.add(new RainbowNoise());
		animations.add(new Particles());
		animations.add(new SortAlgo());
		animations.add(new GradientAnimation());
	}

}
