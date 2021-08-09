package de.lars.remotelightcore.animation.animations;

import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.colors.palette.model.AbstractPalette;
import de.lars.remotelightcore.colors.palette.model.ColorGradient;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingDouble;
import de.lars.remotelightcore.settings.types.SettingGradient;
import de.lars.remotelightcore.utils.color.Color;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class GradientAnimation extends Animation {
	
	private Color[] strip;
	AbstractPalette palette;
	private SettingGradient sGradient;
	private SettingDouble sStepSize;

	public GradientAnimation() {
		super("Gradient");
		sGradient = this.addSetting(new SettingGradient("animations.gradient.gradients", "Gradient", null, null));
		sStepSize = this.addSetting(new SettingDouble("animations.gradient.stepsize", "Resolution", SettingCategory.Intern, null, 0.05, 0.0009, 1.0, 0.005));
	}
	
	@Override
	public void onEnable(int pixels) {
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, pixels);
		super.onEnable(pixels);
	}
	
	@Override
	public Color[] onEffect() {
		palette = sGradient.getPalette();
		
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
