/*-
 * >===license-start
 * RemoteLight
 * ===
 * Copyright (C) 2019 - 2020 Lars O.
 * ===
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * <===license-end
 */

package de.lars.remotelightcore.colors;

import de.lars.remotelightcore.EffectManager;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.colors.palette.AbstractPalette;
import de.lars.remotelightcore.colors.palette.ColorGradient;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.utils.color.Color;
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
	
	public void showGradient(AbstractPalette gradient) {
		if(!(gradient instanceof ColorGradient)) {
			throw new IllegalArgumentException("Supports only color palettes that implements the ColorGradient interface.");
		}
		int pixels = RemoteLightCore.getLedNum();
		float stepSize = 1.0f / pixels;
		((ColorGradient) gradient).setStepSize(stepSize);
		((ColorGradient) gradient).resetStepPosition();
		Color[] strip = new Color[pixels];
		for(int i = 0; i < pixels; i++) {
			strip[i] = gradient.getNext();
		}
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
