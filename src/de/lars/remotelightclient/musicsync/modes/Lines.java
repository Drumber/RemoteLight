package de.lars.remotelightclient.musicsync.modes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.musicsync.MusicEffect;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;
import de.lars.remotelightclient.settings.types.SettingBoolean;
import de.lars.remotelightclient.settings.types.SettingColor;
import de.lars.remotelightclient.settings.types.SettingSelection;
import de.lars.remotelightclient.settings.types.SettingSelection.Model;
import de.lars.remotelightclient.utils.color.ColorUtil;
import de.lars.remotelightclient.utils.color.PixelColorUtils;
import de.lars.remotelightclient.utils.color.RainbowWheel;
import de.lars.remotelightclient.utils.maths.MathHelper;
import de.lars.remotelightclient.utils.maths.TimeUtil;

public class Lines extends MusicEffect {
	
	private SettingsManager s = Main.getInstance().getSettingsManager();
	private String[] colorModes = {"Static", "Rainbow #1", "Rainbow #2", "Spectrum"};
	private int rainbowAllHue = 0;
	
	private Color[] strip;
	private boolean rotate;
	private TimeUtil rotateTimer;
	private int rotateIndex;
	
	private int linesNum; // number of lines
	private int maxLineLength; // max line length

	public Lines() {
		super("Lines");
		s.addSetting(new SettingBoolean("musicsync.lines.rotate", "Rotate", SettingCategory.MusicEffect, "Rotate strip", false));
		this.addOption("musicsync.lines.rotate");
		s.addSetting(new SettingSelection("musicsync.lines.colormode", "Color mode", SettingCategory.MusicEffect, null, colorModes, "Static", Model.ComboBox));
		this.addOption("musicsync.lines.colormode");
		s.addSetting(new SettingColor("musicsync.lines.color", "Color", SettingCategory.MusicEffect, "Static color", Color.RED));
		this.addOption("musicsync.lines.color");
	}
	
	@Override
	public void onEnable() {
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, Main.getLedNum());
		rotateIndex = 0;
		rotateTimer = new TimeUtil(150);
		
		linesNum = strip.length / 10; // number of lines
		maxLineLength = strip.length / linesNum; // maximum line length in pixel
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		rotate = s.getSetting(SettingBoolean.class, "musicsync.lines.rotate").getValue();
		
		// for color mode Rainbow #1
		if(++rainbowAllHue >= RainbowWheel.getRainbow().length)
			rainbowAllHue = 0;
				
		float[] fft = getSoundProcessor().getAmplitudes();
		List<Integer> data = computeFFT(fft, linesNum);
		
		for(int i = 0; i < linesNum; i++) {
			// calculate how many led should glows
			int vol = data.get(i);
			int leds = (int) MathHelper.map(vol, 0, 255, 0, maxLineLength);
						
			int ledIndex = i * 10; // first led index
			for(int l = ledIndex; l < (ledIndex + maxLineLength); l++) {
				if(l < strip.length) { // some safety check
					
					// rotate option: add rotate index to led position
					int pos = l + rotateIndex;
					if(pos >= strip.length)
						pos -= strip.length;
					
					// set color if smaller than leds (see above)
					if(l < ledIndex + leds) {
						strip[pos] = getColor(l, l - ledIndex + 1);
					} else {
						strip[pos] = Color.BLACK;
					}
				}
			}
		}
		
		// rotate option: increase index
		if(rotate) {
			if(isBump())
				rotateIndex += 2;
			if(rotateTimer.hasReached())
				rotateIndex++;
			if(rotateIndex >= strip.length)
				rotateIndex = 0;
		} else {
			rotateIndex = 0;
		}
		
		OutputManager.addToOutput(strip);
		super.onLoop();
	}
	
	
	private Color getColor(int index, int ledVol) {
		String mode = s.getSetting(SettingSelection.class, "musicsync.lines.colormode").getSelected();
		if(mode.equalsIgnoreCase("Rainbow #1")) {
			return RainbowWheel.getRainbow()[rainbowAllHue];
		} else if(mode.equalsIgnoreCase("Rainbow #2")) {
			int mltiplr = RainbowWheel.getRainbow().length / Main.getLedNum();
			return RainbowWheel.getRainbow()[index * mltiplr];
		} else if(mode.equalsIgnoreCase("Spectrum")) {
			int fadeLEDs= maxLineLength - (maxLineLength / 5); // amount of leds which are faded to red
			if(ledVol >= (maxLineLength - fadeLEDs)) {
				float redFade = 1f / fadeLEDs * (fadeLEDs - (maxLineLength - ledVol));
				return ColorUtil.fadeToColor(Color.GREEN, Color.RED, redFade);
			}
			return Color.GREEN;
		}
		// static color
		return s.getSetting(SettingColor.class, "musicsync.lines.color").getValue();
	}
	
	
	private List<Integer> computeFFT(float[] fft, int length) {
		int x, y;
		int b0 = 0;
		
		List<Integer> data = new ArrayList<>();
		
		for(x = 0; x < length; x++) {
			float peak = 0;
			int b1 = (int) Math.pow(2, x*10.0 / (length - 1));
			if(b1 > 1023) b1 = 1023;
			if(b1 <= b0) b1 = b0 + 1; // make sure it uses at least 1 FFT bin
			
			for(; b0 < b1; b0++) {
				if(peak < fft[1 + b0]) peak = fft[1 + b0];
			}
			
			y = (int) (Math.sqrt(peak) * 3 * 2 - 4); // scale it
			
			// normalize values
			int max = 50;
			if(y > max) y = max;
			if(y < 0) y = 0;
			
			y = (int) MathHelper.map(y, 0, max, 0, 255);
			
			// boost values
			y =  (int) (y * (getAdjustment() * 0.4));
			if(y > 255) y = 255;
			
			data.add(y);
		}
		return data;
	}

}
