package de.lars.remotelightclient.scene;

import org.tinylog.Logger;

import de.lars.remotelightclient.network.Client;
import de.lars.remotelightclient.network.Identifier;

public class SceneManager {
	
	private Scene activeScene;
	private boolean active = false;
	
	public SceneManager() {
		
	}
	
	public boolean isActive() {
		return (activeScene != null);
	}
	
	public Scene getActiveScene() {
		return activeScene;
	}
	
	public void start(Scene scene) {
		if(activeScene != null) {
			activeScene.onDisable();
		}
		if(scene != null) {
			scene.onEnable();
		}
		activeScene = scene;
		this.loop();
	}
	
	public void stop() {
		if(activeScene != null) {
			activeScene.onDisable();
		}
		activeScene = null;
		Client.send(new String[] {Identifier.WS_COLOR_OFF});
	}
	
	private void loop() {
		if(!active) {
			active = true;
			Logger.info("Starting Scene Thread.");
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					while(activeScene != null) {
						
						activeScene.onLoop();
						int delay = activeScene.getDelay();
						
						try {
							Thread.sleep(delay);
						} catch (InterruptedException e) {
							Logger.error("Scene Thread could not wait for delay! (delay: " + delay + ")");
							e.printStackTrace();
						}
					}
					active = false;
					Logger.info("Stopped Scene Thread.");
				}
			}).start();
		}
	}

}
