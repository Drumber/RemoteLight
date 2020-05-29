package de.lars.remotelightcore.musicsync.modes;

import java.awt.Color;
import java.util.Random;

import de.lars.remotelightcore.Main;
import de.lars.remotelightcore.musicsync.MusicEffect;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.utils.color.ColorUtil;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;
import de.lars.remotelightcore.utils.maths.MathHelper;

public class Flame extends MusicEffect {
	/* inspired by Andrew Tuline:
	 * https://github.com/atuline/FastLED-SoundReactive/blob/master/notasound/besin.h
	 */
	
	private SettingsManager s = Main.getInstance().getSettingsManager();
	private Color[] strip;
	private int hue;

	public Flame() {
		super("Flame");
		s.addSetting(new SettingColor("musicsync.flame.color", "Color", SettingCategory.MusicEffect, null, Color.RED));
		this.addOption("musicsync.flame.color");
		s.addSetting(new SettingBoolean("musicsync.flame.rainbow", "Rainbow", SettingCategory.MusicEffect, null, false));
		this.addOption("musicsync.flame.rainbow");
	}
	
	@Override
	public void onEnable() {
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, Main.getLedNum());
		hue = 0;
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		Color color = ((SettingColor) s.getSettingFromId("musicsync.flame.color")).getValue();
		boolean rainbow = ((SettingBoolean) s.getSettingFromId("musicsync.flame.rainbow")).getValue();
		int ledNum = Main.getLedNum();
		
		if(rainbow) {
			if(isBump()) {
				hue += 8;
			} else {
				hue += 1 + new Random().nextInt(4);
			}
			if(hue >= RainbowWheel.getRainbow().length) hue = 0;
			color = RainbowWheel.getRainbow()[hue];
		}
		
		float vol = (float) (getSpl() / getMaxSpl());	// volume (0..1)
		vol = MathHelper.easeInCubic(vol); // easing value a little
		
		int brightness = (int) MathHelper.lerp(0.0f, 255.0f, vol);
		brightness *= (getAdjustment() / 4);
		color = ColorUtil.dimColorSimple(color, 255 - brightness);
		
		strip[ledNum/2] = color;
		strip[ledNum/2 - 1] = color;
		
		OutputManager.addToOutput(strip);
		PixelColorUtils.shiftCenter(1);
		strip = ColorUtil.dimColorSimple(strip, 5); // fade to black
		super.onLoop();
	}

}
