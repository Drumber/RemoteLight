package de.lars.remotelightcore.animation.animations;

import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.colors.palette.model.AbstractPalette;
import de.lars.remotelightcore.colors.palette.model.ColorGradient;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.settings.types.SettingGradient;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightcore.settings.types.SettingSelection.Model;
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
	private SettingSelection sColorMode;
	private final String[] MODES = {"Static", "Gradient"};
	private SettingColor sColor;
	private SettingGradient sGradient;

	public Sawtooth() {
		super("Sawtooth");
		sPeriod = this.addSetting(new SettingInt("animation.sawtooth.period", "Period", SettingCategory.Intern, null, 60, 1, 500, 5));
		sDirection = this.addSetting(new SettingSelection("animation.sawtooth.direction", "Direction", SettingCategory.Intern, null, DIRECTIONS, DIRECTIONS[1], Model.ComboBox));
		sInvert = this.addSetting(new SettingBoolean("animation.sawtooth.invert", "Invert", SettingCategory.Intern, null, false));
		sColorMode = this.addSetting(new SettingSelection("animation.sawtooth.colormode", "Color Mode", SettingCategory.Intern, null, MODES, MODES[0], Model.ComboBox));
		sColor = this.addSetting(new SettingColor("animation.sawtooth.color", "Color", SettingCategory.Intern, null, Color.RED));
		sGradient = this.addSetting(new SettingGradient("animatuon.sawtooth.gradient", "Gradient", null, null));
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
		String mode = sColorMode.get();
		if(mode.equals("Static")) {
			return sColor.get();
		}
		AbstractPalette palette = sGradient.getPalette();
		if(palette instanceof ColorGradient) {
			((ColorGradient) palette).setStepSize(1.0f / strip.length);
			if(index == 0) {
				((ColorGradient) palette).resetStepPosition();
			}
			return palette.getNext();
		}
		int gradientIndex = (int) MathHelper.map(index, 0, strip.length, 0, palette.size());
		return palette.getColorAtIndex(gradientIndex);
	}
	
	@Override
	public void onSettingUpdate() {
		String mode = sColorMode.get();
		this.hideSetting(sColor, !mode.equals("Static"));
		this.hideSetting(sGradient, !mode.equals("Gradient"));
		super.onSettingUpdate();
	}

}
