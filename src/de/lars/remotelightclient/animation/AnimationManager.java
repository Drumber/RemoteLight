package de.lars.remotelightclient.animation;

import org.tinylog.Logger;

import de.lars.remotelightclient.network.Client;
import de.lars.remotelightclient.network.Identifier;

public class AnimationManager {
	
	private Animation activeAnimation;
	private boolean active;
	private int delay = 50;
	
	public AnimationManager() {
		
	}
	
	public boolean isActive() {
		return (activeAnimation != null);
	}
	
	public Animation getActiveAnimation() {
		return activeAnimation;
	}
	
	public void start(Animation animation) {
		if(activeAnimation != null) {
			activeAnimation.onDisable();
		}
		Client.send(new String[] {Identifier.WS_COLOR_OFF});
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
