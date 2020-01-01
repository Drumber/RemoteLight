package de.lars.remotelightclient.animation.animations;

import java.awt.Color;
import java.math.BigDecimal;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.animation.Animation;
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;
import de.lars.remotelightclient.settings.types.SettingBoolean;
import de.lars.remotelightclient.settings.types.SettingColor;
import de.lars.remotelightclient.utils.ColorUtil;
import de.lars.remotelightclient.utils.PixelColorUtils;
import de.lars.remotelightclient.utils.RainbowWheel;

public class ColorWave extends Animation {

	private Color color, oldColor, newColor;
	private double step = 1.0;
	private BigDecimal fade = new BigDecimal("0.0");
	
	public ColorWave() {
		super("ColorWave");
		color = RainbowWheel.getRandomColor();
		oldColor = color;
		newColor = RainbowWheel.getRandomColor();
		this.addSetting(new SettingBoolean("animation.colorwave.randomcolor", "Random color", SettingCategory.Intern, null, false));
		this.addSetting(new SettingColor("animation.colorwave.color", "Color", SettingCategory.Intern,	null, Color.RED));
		this.addSetting(new SettingBoolean("animation.colorwave.wave", "Wave", SettingCategory.Intern, "Increase and decrease brightness dynamically", true));
	}
	
	@Override
	public void onLoop() {
		if(!((SettingBoolean) getSetting("animation.colorwave.randomcolor")).getValue()) {
			color = ((SettingColor) getSetting("animation.colorwave.color")).getValue();
		}
		
		PixelColorUtils.shiftRight(1);
		PixelColorUtils.setPixel(0, calcWave(step, color));
		
		step += 0.01 * (60 / Main.getLedNum());
		if(step >= 2.0) {
			step = 0;
		}
		
		// fade to new color
		color = ColorUtil.fadeToColor(oldColor, newColor, fade.floatValue());
		
		fade = fade.add(new BigDecimal("0.01"));
		if(fade.floatValue() > 1.0) {
			fade = new BigDecimal("0.0");
			oldColor = newColor;
			newColor = RainbowWheel.getRandomColor();
		}
		super.onLoop();
	}
	
	private Color calcWave(double stepping, Color c) {
		int dim = (int) (40 * Math.sin(Math.PI * stepping) + 60);
		if(!((SettingBoolean) getSetting("animation.colorwave.wave")).getValue()) {
			return c;
		}
		return ColorUtil.dimColor(c, dim);
	}

}
