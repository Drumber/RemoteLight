package de.lars.remotelightclient.musicsync;

import org.tinylog.Logger;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.EffectManager.EffectType;
import de.lars.remotelightclient.musicsync.sound.InputFrame;
import de.lars.remotelightclient.musicsync.sound.SoundProcessing;
import de.lars.remotelightclient.network.Client;
import de.lars.remotelightclient.network.Identifier;

public class MusicSyncManager {
	
	private MusicEffect activeEffect;
	private boolean active = false;
	private MusicSyncUtils musicUtils;
	private SoundProcessing soundProcessor;
	private InputFrame inputFrame;
	private double sensitivity = 1;
	private double volume;
	private float pitch;
	private double pitchTime;
	
	public MusicSyncManager() {
		if(inputFrame != null) {
			inputFrame.dispose();
		}
		inputFrame = new InputFrame(this);
		inputFrame.pack();
		
		if(!SoundProcessing.isMixerSet())
			return;
		if(soundProcessor != null) {
			soundProcessor.stop();
		}
		soundProcessor = new SoundProcessing(inputFrame, this);
		musicUtils = new MusicSyncUtils();
	}
	
	public void openGUI() {
		if(inputFrame == null) {
			inputFrame = new InputFrame(this);
			inputFrame.pack();
		}
		inputFrame.setVisible(true);
	}
	
	public void closeGUI() {
		if(inputFrame != null) {
			inputFrame.dispose();
			System.out.println("Dispose InputFrame");
		}
	}
	
	public void newSoundProcessor() {
		if(soundProcessor != null) {
			soundProcessor.stop();
		}
		soundProcessor = new SoundProcessing(inputFrame, this);
	}
	
	public void soundToLight(float pitch, double rms, double time) {
		this.volume = rms;
		this.pitch = pitch;
		this.pitchTime = time;
	}
	
	public void setSensitivity(double sensitivity) {
		this.sensitivity = sensitivity;
	}
	
	public double getSensitivity() {
		return sensitivity;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public MusicEffect getActiveEffect() {
		return activeEffect;
	}
	
	public void start(MusicEffect effect) {
		Main.getInstance().getEffectManager().stopAllExceptFor(EffectType.Animation);
		if(activeEffect != null) {
			activeEffect.onDisable();
		} else {
			this.newSoundProcessor();
		}
		if(effect != null) {
			effect.onEnable();
		}
		activeEffect = effect;
		this.loop();
	}
	
	public void stop() {
		if(activeEffect != null) {
			activeEffect.onDisable();
		}
		activeEffect = null;
		soundProcessor.stop();
		inputFrame.dispose();
		Client.send(new String[] {Identifier.WS_COLOR_OFF});
	}
	
	private void loop() {
		if(!active) {
			active = true;
			Logger.info("Starting MusicSync Thread.");
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					while(activeEffect != null) {
						musicUtils.process(pitch, volume, pitchTime, sensitivity, soundProcessor);
						volume = musicUtils.getVolume();
						activeEffect.setBump(musicUtils.isBump());
						activeEffect.setVolume(volume);
						activeEffect.setPitch(pitch);
						activeEffect.setPitchTime(pitchTime);
						activeEffect.setSoundProcessor(soundProcessor);
						activeEffect.setSensitivity(sensitivity);
						activeEffect.setMaxSpl(musicUtils.getMaxSpl());
						activeEffect.setMinSpl(musicUtils.getMinSpl());
						activeEffect.setSpl(musicUtils.getSpl());
						
						activeEffect.onLoop();
						
						try {
							Thread.sleep(40);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					active = false;
					Logger.info("Stopped MusicSync Thread.");
				}
			}).start();
		}
	}

}
