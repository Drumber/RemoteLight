package de.lars.remotelightclient.out;

import java.awt.Color;

import org.tinylog.Logger;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.settings.types.SettingBoolean;
import de.lars.remotelightclient.settings.types.SettingObject;

public class OutputManager {
	
	private volatile Output activeOutput;
	private volatile static Color[] outputPixels;
	private int delay = 100;
	private boolean active;
	
	public OutputManager() {
		active = false;
	}

	public Output getActiveOutput() {
		return activeOutput;
	}

	public void setActiveOutput(Output activeOutput) {
		this.activeOutput = activeOutput;
		this.loop();
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
	
	public void close() {
		setEnabled(false);
		//save last output before closing
		Main.getInstance().getSettingsManager().getSettingFromType(new SettingObject("out.lastoutput", null, null)).setValue(activeOutput);
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
