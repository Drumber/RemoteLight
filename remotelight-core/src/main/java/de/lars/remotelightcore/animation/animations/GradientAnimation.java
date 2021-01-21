package de.lars.remotelightcore.animation.animations;

import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingDouble;
import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightcore.settings.types.SettingSelection.Model;
import de.lars.remotelightcore.utils.color.Color;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.palette.AbstractPalette;
import de.lars.remotelightcore.utils.color.palette.ColorGradient;
import de.lars.remotelightcore.utils.color.palette.Palettes;

public class GradientAnimation extends Animation {
	
	private Color[] strip;
	AbstractPalette palette;
	private SettingSelection sGradient;
	private SettingDouble sStepSize;

	public GradientAnimation() {
		super("Gradient");
		String[] values = Palettes.getNames().toArray(new String[0]);
		sGradient = this.addSetting(new SettingSelection("animations.gradient.gradients", "Gradient", SettingCategory.Intern, null, values, values[0], Model.ComboBox));
		sStepSize = this.addSetting(new SettingDouble("animations.gradient.stepsize", "Resolution", SettingCategory.Intern, null, 0.05, 0.0009, 1.0, 0.005));
	}
	
	@Override
	public void onEnable(int pixels) {
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, pixels);
		super.onEnable(pixels);
	}
	
	@Override
	public Color[] onEffect() {
		palette = Palettes.getPalette(sGradient.getSelected());
		
		if(palette instanceof ColorGradient) {
			((ColorGradient) palette).setStepSize(sStepSize.get().floatValue());
			((ColorGradient) palette).setReverseOnEnd(true);
		}
		
		if(strip.length > 1) {
			for(int i = strip.length-1; i > 0; i--) {
				strip[i] = strip[i-1];
			}
		}
		
		strip[0] = palette.getNext();
		
		return strip;
	}
	
	@Override
	public void onSettingUpdate() {
		if(palette != null) {
			this.hideSetting(sStepSize, !(palette instanceof ColorGradient));
		}
		super.onSettingUpdate();
	}
	
}
