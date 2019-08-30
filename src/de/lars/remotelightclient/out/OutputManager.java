package de.lars.remotelightclient.out;

import java.awt.Color;

import org.tinylog.Logger;

public class OutputManager {
	
	private volatile Output activeOutput;
	private volatile static Color[] outputPixels;
	private int delay = 100;
	private boolean active;
	
	public OutputManager() {
		active = false;
		this.loop();
	}

	public Output getActiveOutput() {
		return activeOutput;
	}

	public void setActiveOutput(Output activeOutput) {
		this.activeOutput = activeOutput;
	}
	
	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	public int getDelay() {
		return delay;
	}
	
	public void setEnabled(boolean enabled) {
		active = enabled;
	}
	
	public boolean isEnabled() {
		return active;
	}
	
	public static void addToOutput(Color[] pixels) {
		outputPixels = pixels;
	}
	
	private void loop() {
		if(!active) {
			active = true;
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					Logger.info("Started output loop.");
					
					while(active) {
						if((outputPixels != null) && (activeOutput != null)) {
							activeOutput.onOutput(outputPixels);
							
							try {
								Thread.sleep(delay);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					Logger.info("Stopped output loop.");
				}
			}).start();
		}
	}

}
