package de.lars.remotelightclient.musicsync.modes;

import java.awt.Color;
import java.util.Arrays;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.musicsync.MusicEffect;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;
import de.lars.remotelightclient.settings.types.SettingBoolean;
import de.lars.remotelightclient.settings.types.SettingColor;
import de.lars.remotelightclient.utils.ColorUtil;
import de.lars.remotelightclient.utils.PixelColorUtils;
import de.lars.remotelightclient.utils.RainbowWheel;

public class Visualizer extends MusicEffect {
	
	private SettingsManager s = Main.getInstance().getSettingsManager();
	private Color color = Color.RED;
	private boolean rainbow = false;

	public Visualizer() {
		super("Visualizer");
		
		s.addSetting(new SettingColor("musicsync.visualizer.color", "Color", SettingCategory.MusicEffect, "", Color.RED));
		this.addOption("musicsync.visualizer.color");
		s.addSetting(new SettingBoolean("musicsync.visualizer.rainbow", "Rainbow", SettingCategory.MusicEffect, "", false));
		this.addOption("musicsync.visualizer.rainbow");
	}
	
	@Override
	public void onLoop() {
		this.initOptions();
		
		float[] ampl = getSoundProcessor().getAmplitudes(); //amplitudes
		int bin15khz = getSoundProcessor().hzToBin(12000); //get binIndex of 15kHz
		if(ampl.length > bin15khz) {
			ampl = Arrays.copyOfRange(ampl, 0, bin15khz); //we only want to show frequencies up to 15khz
		}
		
		int frequncLed = ampl.length / Main.getLedNum(); //how many frequencies does a led show (frequency range per led)
		
		for(int i = 0; i < Main.getLedNum(); i++) {
			int brightness = amplitudeToBrightness(ampl[frequncLed * i]);
			
			Color c = ColorUtil.dimColor(getColor(frequncLed*i, i), brightness);
			
			PixelColorUtils.setPixel(i, c);
		}
		
		super.onLoop();
	}
	
	private int amplitudeToBrightness(double ampl) {
		if(ampl < 3.0) {
			return 0;
		}
		//System.out.println((int) (ampl * getAdjustment()));
		return (int) (ampl * getAdjustment());
	}
	
	private Color getColor(double ampl, int led) {
		if(rainbow) {
			int mltiplr = RainbowWheel.getRainbow().length / Main.getLedNum();
			return RainbowWheel.getRainbow()[led * mltiplr];
		} else {
			return color;
		}
	}
	
	private void initOptions() {
		color = ((SettingColor) s.getSettingFromId("musicsync.visualizer.color")).getValue();
		rainbow = ((SettingBoolean) s.getSettingFromId("musicsync.visualizer.rainbow")).getValue();
	}

}
