package de.lars.remotelightclient.animation.animations;

import java.awt.Color;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.animation.Animation;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;
import de.lars.remotelightclient.settings.types.SettingInt;
import de.lars.remotelightclient.utils.MathHelper;
import de.lars.remotelightclient.utils.PixelColorUtils;
import de.lars.remotelightclient.utils.RainbowWheel;

public class Juggle extends Animation {
	
	private Color[] strip;

	public Juggle() {
		super("Juggle");
		this.addSetting(new SettingInt("animation.juggle.dotsnumber", "Number of dots", SettingCategory.Intern, null, 8, 1, 30, 1));
	}
	
	@Override
	public void onEnable() {
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, Main.getLedNum());
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		int dotsNum = ((SettingInt) getSetting("animation.juggle.dotsnumber")).getValue();
		
		dim();
		int dotHue = 0;
		
		for(int i = 0; i < dotsNum; i++) {
			int pos = MathHelper.beatsin16(i+7, strip.length, 0, 0.01);
			strip[pos] = RainbowWheel.getRainbow()[dotHue];
			
			dotHue += 32;
			if(dotHue >= RainbowWheel.getRainbow().length)
				dotHue = 0;
		}
		
		OutputManager.addToOutput(strip);
		super.onLoop();
	}
	
	private void dim() {
		for(int i = 0; i < strip.length; i++) {
			Color c = strip[i];
			int r = c.getRed() - 20;
			int g = c.getGreen() - 20;
			int b = c.getBlue() - 20;
			if(r < 0) r = 0;
			if(g < 0) g = 0;
			if(b < 0) b = 0;
			strip[i] = new Color(r, g, b);
		}
	}

}
