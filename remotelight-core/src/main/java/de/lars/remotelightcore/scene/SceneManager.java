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

package de.lars.remotelightcore.scene;

import de.lars.remotelightcore.utils.color.Color;
import java.util.ArrayList;
import java.util.List;

import org.tinylog.Logger;

import de.lars.remotelightcore.EffectManager;
import de.lars.remotelightcore.EffectManagerHelper.EffectType;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.scene.scenes.Fire;
import de.lars.remotelightcore.scene.scenes.Jungle;
import de.lars.remotelightcore.scene.scenes.NorthernLights;
import de.lars.remotelightcore.scene.scenes.Ocean;
import de.lars.remotelightcore.scene.scenes.SnowSparkle;
import de.lars.remotelightcore.scene.scenes.Space;
import de.lars.remotelightcore.scene.scenes.Sunset;
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
		RemoteLightCore.getInstance().getEffectManagerHelper().stopAllExceptFor(EffectType.Scene);
		if(activeScene != null) {
			activeScene.onDisable();
			OutputManager.addToOutput(PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum()));
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
		turnOffLeds();
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
