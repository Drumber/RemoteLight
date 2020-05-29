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
package de.lars.remotelightcore.scene;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.tinylog.Logger;

import de.lars.remotelightcore.EffectManager;
import de.lars.remotelightcore.Main;
import de.lars.remotelightcore.EffectManagerHelper.EffectType;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.scene.scenes.*;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class SceneManager extends EffectManager {
	
	private Scene activeScene;
	private List<Scene> scenes;
	private boolean active = false;
	
	public SceneManager() {
		scenes = new ArrayList<Scene>();
		this.registerScenes();
	}
	
	@Override
	public String getName() {
		return "SceneManager";
	}
	
	@Override
	public boolean isActive() {
		return (activeScene != null);
	}
	
	public Scene getActiveScene() {
		return activeScene;
	}
	
	public List<Scene> getScenes() {
		return this.scenes;
	}
	
	public void start(Scene scene) {
		Main.getInstance().getEffectManagerHelper().stopAllExceptFor(EffectType.Scene);
		if(activeScene != null) {
			activeScene.onDisable();
		}
		if(scene != null) {
			scene.onEnable();
		}
		activeScene = scene;
		this.loop();
	}
	
	@Override
	public void stop() {
		if(activeScene != null) {
			activeScene.onDisable();
		}
		activeScene = null;
		OutputManager.addToOutput(PixelColorUtils.colorAllPixels(Color.BLACK, Main.getLedNum()));
	}
	
	private void loop() {
		if(!active) {
			active = true;
			Logger.info("Starting Scene Thread.");
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					while(activeScene != null) {
						try {
						activeScene.onLoop();
						
						} catch(Exception e) {
							Logger.error(e, "There was an error executing the scene '" + activeScene.getDisplayname() + "'.");
							// stop on exception
							stop();
							break;
						}
						
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
	
	private void registerScenes() {
		scenes.add(new Sunset());
		scenes.add(new Fire());
		scenes.add(new Ocean());
		scenes.add(new Jungle());
		scenes.add(new NorthernLights());
		scenes.add(new Space());
		scenes.add(new SnowSparkle());
		
	}

}
