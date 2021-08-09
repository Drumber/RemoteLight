package de.lars.remotelightcore.animation.animations;

import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightcore.utils.color.Color;
import de.lars.remotelightcore.utils.color.ColorUtil;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.maths.MathHelper;

public class Sawtooth extends Animation {
	
	private Color[] strip;
	private long p = 0;
	private SettingInt sPeriod;
	private SettingSelection sDirection;
	private final String[] DIRECTIONS = {"Left", "Right"};
	private SettingBoolean sInvert;
	private SettingColor sColor;

	public Sawtooth() {
		super("Sawtooth");
		sPeriod = this.addSetting(new SettingInt("animation.sawtooth.period", "Period", SettingCategory.Intern, null, 60, 1, 500, 5));
		sDirection = this.addSetting(new SettingSelection("animation.sawtooth.direction", "Direction", SettingCategory.Intern, null, DIRECTIONS, DIRECTIONS[1], SettingSelection.Model.ComboBox));
		sInvert = this.addSetting(new SettingBoolean("animation.sawtooth.invert", "Invert", SettingCategory.Intern, null, false));
		sColor = this.addSetting(new SettingColor("animation.sawtooth.color", "Color", SettingCategory.Intern, null, Color.RED));
	}
	
	@Override
	public void onEnable(int pixels) {
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, pixels);
		p = 0;
		super.onEnable(pixels);
	}
	
	@Override
	public Color[] onEffect() {
		double period = 1000.0 / sPeriod.get();
		boolean inverted = sInvert.get();
		
		for(int i = 0; i < strip.length; i++) {
			double div = (i + p) / period;
			double val = 2 * (div - Math.floor(0.5 + div));
			
			int brightness = (int) MathHelper.map(val, -1.0, 1.0, 0, 100);
			Color c = ColorUtil.dimColor(getColor(i), brightness);
			
			int index = !inverted ? i : strip.length - 1 - i;
			strip[index] = c;
			
		}
		
		p += sDirection.get().equals("Left") ? 1 : -1;
		return strip;
	}
	
	private Color getColor(int index) {
		return sColor.get();
	}

}
