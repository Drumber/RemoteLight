package de.lars.remotelightclient;

import de.lars.remotelightclient.animation.AnimationManager;
import de.lars.remotelightclient.musicsync.MusicSyncManager;
import de.lars.remotelightclient.scene.SceneManager;

public class EffectManager {
	
	private AnimationManager am;
	private MusicSyncManager msm;
	private SceneManager sm;
	
	public EffectManager() {
		Main main = Main.getInstance();
		am = main.getAnimationManager();
		msm = main.getMusicSyncManager();
		sm = main.getSceneManager();
	}
	
	public void stopAll() {
		am.stop();
		msm.stop();
		sm.stop();
	}

}
