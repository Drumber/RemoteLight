package de.lars.remotelightcore.colors;

import java.awt.Color;

import de.lars.remotelightcore.EffectManager;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.utils.color.ColorUtil;
import de.lars.remotelightcore.utils.color.PixelColorUtils;

public class ColorManager extends EffectManager {
	
	private Color lastColor;

	@Override
	public String getName() {
		return "ColorManager";
	}

	@Override
	public void stop() {
		showColor(Color.BLACK);
	}
	
	public void showColor(Color color) {
		RemoteLightCore.getInstance().getEffectManagerHelper().stopAll();
		if(color == null)
			color = Color.BLACK;
		lastColor = color;
		Color[] strip = PixelColorUtils.colorAllPixels(color, RemoteLightCore.getLedNum());
		OutputManager.addToOutput(strip);
	}
	
	public Color getLastColor() {
		return lastColor;
	}
	
	public String getLastColorHex() {
		if(lastColor != null) {
			return String.format("#%02x%02x%02x",
					lastColor.getRed(),
					lastColor.getGreen(),
					lastColor.getBlue());  
		}
		return null;
	}

	@Override
	public boolean isActive() {
		return lastColor != null && !ColorUtil.isBlack(lastColor);
	}

}
