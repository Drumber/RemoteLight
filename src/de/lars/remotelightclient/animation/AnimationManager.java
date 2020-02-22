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
package de.lars.remotelightclient.animation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.tinylog.Logger;

import de.lars.remotelightclient.EffectManager.EffectType;
import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.animation.animations.BouncingBalls;
import de.lars.remotelightclient.animation.animations.Breath;
import de.lars.remotelightclient.animation.animations.Close;
import de.lars.remotelightclient.animation.animations.ColorWave;
import de.lars.remotelightclient.animation.animations.Confetti;
import de.lars.remotelightclient.animation.animations.Fade;
import de.lars.remotelightclient.animation.animations.Jump;
import de.lars.remotelightclient.animation.animations.Meteor;
import de.lars.remotelightclient.animation.animations.Open;
import de.lars.remotelightclient.animation.animations.Rainbow;
import de.lars.remotelightclient.animation.animations.RandomColor;
import de.lars.remotelightclient.animation.animations.TwoColors;
import de.lars.remotelightclient.animation.animations.RunningLight;
import de.lars.remotelightclient.animation.animations.Scanner;
import de.lars.remotelightclient.animation.animations.Shake;
import de.lars.remotelightclient.animation.animations.Snake;
import de.lars.remotelightclient.animation.animations.SoftTwinkles;
import de.lars.remotelightclient.animation.animations.TheaterChase;
import de.lars.remotelightclient.animation.animations.Twinkle;
import de.lars.remotelightclient.animation.animations.Wipe;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.settings.Setting;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.utils.PixelColorUtils;

public class AnimationManager {
	
	private Animation activeAnimation;
	private List<Animation> animations;
	private boolean active;
	private int delay = 50;
	
	public AnimationManager() {
		animations = new ArrayList<Animation>();
		this.registerAnimations();
	}
	
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
		SettingsManager sm = Main.getInstance().getSettingsManager();
		List<Setting> tmp = new ArrayList<>();
		if(getActiveAnimation() == null) {
			return tmp;
		}
		for(String s : getActiveAnimation().getOptions()) {
			tmp.add(sm.getSettingFromId(s));
		}
		return tmp;
	}
	
	public void start(Animation animation) {
		Main.getInstance().getEffectManager().stopAllExceptFor(EffectType.Animation);
		if(activeAnimation != null) {
			activeAnimation.onDisable();
		}
		OutputManager.addToOutput(PixelColorUtils.colorAllPixels(Color.BLACK, Main.getLedNum()));
		if(animation != null) {
			animation.onEnable();
		}
		activeAnimation = animation;
		this.loop();
	}
	
	public void stop() {
		if(activeAnimation != null) {
			activeAnimation.onDisable();
			activeAnimation = null;
		}
		OutputManager.addToOutput(PixelColorUtils.colorAllPixels(Color.BLACK, Main.getLedNum()));
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
	}

}
