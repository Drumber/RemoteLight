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

package de.lars.remotelightcore.animation.animations;

import java.awt.Color;

import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class TheaterChase extends Animation {
	
	private boolean swtch;
	private int hsv = 0;
	private int counter = 0;
	private Color[] strip;

	public TheaterChase() {
		super("TheaterChase");
	}
	
	@Override
	public void onEnable() {
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, RemoteLightCore.getLedNum());
		for(int i = 0; i < strip.length * 2; i++) {
			onLoop();
		}
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		if(swtch) {
			
			for(int i = strip.length-1; i > 0; i--) {
				strip[i] = strip[i-1];
			}
			
			if(counter++ == 2) {
				strip[0] = RainbowWheel.getRainbow()[hsv];
				counter = 0;
			} else {
				strip[0] = Color.BLACK;
			}
			
			hsv++;
			if(hsv >= RainbowWheel.getRainbow().length) {
				hsv = 0;
			}
			
			OutputManager.addToOutput(strip);
		}
		swtch = !swtch;
		
		super.onLoop();
	}
	
}
