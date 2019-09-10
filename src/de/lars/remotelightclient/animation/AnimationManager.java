package de.lars.remotelightclient.animation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.tinylog.Logger;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.animation.animations.Rainbow;
import de.lars.remotelightclient.animation.animations.RunningLight;
import de.lars.remotelightclient.animation.animations.Scanner;
import de.lars.remotelightclient.animation.animations.Wipe;
import de.lars.remotelightclient.out.OutputManager;
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
	
	public void start(Animation animation) {
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
	
	private void registerAnimations() {
		animations.add(new Rainbow());
		animations.add(new RunningLight());
		animations.add(new Scanner());
		animations.add(new Wipe());
	}

}
