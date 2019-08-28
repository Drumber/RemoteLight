package de.lars.remotelightclient.animation;

import java.util.ArrayList;
import java.util.List;

import org.tinylog.Logger;

import de.lars.remotelightclient.animation.animations.Rainbow;
import de.lars.remotelightclient.animation.animations.RunningLight;
import de.lars.remotelightclient.animation.animations.Scanner;
import de.lars.remotelightclient.animation.animations.Wipe;
import de.lars.remotelightclient.network.Client;
import de.lars.remotelightclient.network.Identifier;

public class AnimationManager {
	
	private List<Animation> animations = new ArrayList<Animation>();
	private Animation activeAnimation;
	private boolean active;
	private int delay = 50;
	
	/*
	 * \/ Register new Animations here \/
	 */
	private void registerAnimations() {
		this.addAnimation(new Rainbow("Rainbow", "Rainbow"));
		this.addAnimation(new RunningLight("RunningLight", "RunningLight"));
		this.addAnimation(new Scanner("Scanner", "Scanner"));
		this.addAnimation(new Wipe("Wipe", "Wipe"));
	}
	
	public AnimationManager() {
		this.registerAnimations();
		Logger.info("Registered " + this.animations.size() + " Animations.");
	}
	
	public void addAnimation(Animation animation) {
		this.animations.add(animation);
	}
	
	public List<Animation> getAnimations() {
		return this.animations;
	}
	
	public Animation getAnimationByName(String name) {
		for(Animation ani : this.animations) {
			if(ani.getName().trim().equalsIgnoreCase(name)) {
				return ani;
			}
		}
		Logger.debug(name + " not found!");
		return null;
	}
	
	public Animation getAnimationByClass(Class <? extends Animation> cls) {
		for(Animation ani : this.animations) {
			if(ani.getClass() == cls) {
				return ani;
			}
		}
		Logger.debug(cls + " not found!");
		return null;
	}
	
	public boolean isAnimationRegistered(String name) {
		return (this.getAnimationByName(name) != null);
	}
	
	public void start(Animation animation) {
		if(activeAnimation != null) {
			activeAnimation.setEnabled(false);
		}
		if(animation != null) {
			animation.setEnabled(true);
		}
		activeAnimation = animation;
		this.loop();
	}
	
	public void start(String animationName) {
		Animation ani = this.getAnimationByName(animationName);
		if(ani != null) {
			this.start(ani);
		}
	}
	
	public void stop() {
		if(activeAnimation != null) {
			activeAnimation.setEnabled(false);
			activeAnimation = null;
		}
		Client.send(new String[] {Identifier.WS_COLOR_OFF});
	}
	
	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	private void loop() {
		if(!active) {
			active = true;
			Logger.info("Starting Animation Thread.");
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					while(activeAnimation != null) {
						activeAnimation.onLoop();
						
						if(activeAnimation.isAdjustable()) {
							try {
								Thread.sleep(delay);
							} catch (InterruptedException e) {
								Logger.error("Animation Thread could not wait for delay! (delay: " + delay + ")");
								e.printStackTrace();
							}
						} else {
							try {
								Thread.sleep(activeAnimation.getDelay());
							} catch (InterruptedException e) {
								Logger.error("Animation Thread could not wait for delay! (delay: " + activeAnimation.getDelay() + ")");
								e.printStackTrace();
							}
						}
					}
					active = false;
					Logger.info("Stopped Animation Thread.");
				}
			}).start();
		}
	}

}
