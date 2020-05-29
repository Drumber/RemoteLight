package de.lars.remotelightcore.musicsync.modes;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import de.lars.remotelightcore.Main;
import de.lars.remotelightcore.musicsync.MusicEffect;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.maths.TimeUtil;

public class Strobe extends MusicEffect {
	
	private SettingsManager s = Main.getInstance().getSettingsManager();
	private Timer timer;
	private TimeUtil timeUtil;
	private boolean triggered;
	private int flashRatio = 4;
	private int flashDuration;
	private boolean flashSwitch;
	private Color color = Color.WHITE;

	public Strobe() {
		super("Strobe");
		
		s.addSetting(new SettingInt("musicsync.strobe.flashratio", "Flash ratio (per second)", SettingCategory.MusicEffect, "Flashes per second", 4, 0, 100, 1));
		this.addOption("musicsync.strobe.flashratio");
		s.addSetting(new SettingInt("musicsync.strobe.flashduration", "Flash duration (in ms)", SettingCategory.MusicEffect, "", 1500, 100, 50000, 100));
		this.addOption("musicsync.strobe.flashduration");
		s.addSetting(new SettingColor("musicsync.strobe.color", "Color", SettingCategory.MusicEffect, "", Color.WHITE));
		this.addOption("musicsync.strobe.color");
		
		flashRatio = ((SettingInt) s.getSettingFromId("musicsync.strobe.flashratio")).getValue();
		flashDuration = ((SettingInt) s.getSettingFromId("musicsync.strobe.flashduration")).getValue();
		timer = new Timer(1000 / flashRatio, flashListener);
		timeUtil = new TimeUtil(flashDuration);
	}
	
	@Override
	public void onEnable() {
		triggered = false;
		timer.stop();
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		timer.stop();
		super.onDisable();
	}
	
	@Override
	public void onLoop() {
		color = ((SettingColor) s.getSettingFromId("musicsync.strobe.color")).getValue();
		flashRatio = ((SettingInt) s.getSettingFromId("musicsync.strobe.flashratio")).getValue();
		flashDuration = ((SettingInt) s.getSettingFromId("musicsync.strobe.flashduration")).getValue();
		timer.setDelay(1000 / flashRatio);
		timeUtil.setInterval(flashDuration);
		
		if(triggered && timeUtil.hasReached()) {
			triggered = false;
			timer.stop();
			OutputManager.addToOutput(PixelColorUtils.colorAllPixels(Color.BLACK, Main.getLedNum()));
		}
		
		if(this.isBump() && !triggered) {
			flashSwitch = true;
			triggered = true;
			timer.start();
			timeUtil.reset();
		}
		
		super.onLoop();
	}
	
	private ActionListener flashListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(flashSwitch)
				OutputManager.addToOutput(PixelColorUtils.colorAllPixels(color, Main.getLedNum()));
			else
				OutputManager.addToOutput(PixelColorUtils.colorAllPixels(Color.BLACK, Main.getLedNum()));
			flashSwitch = !flashSwitch;
		}
	};

}
