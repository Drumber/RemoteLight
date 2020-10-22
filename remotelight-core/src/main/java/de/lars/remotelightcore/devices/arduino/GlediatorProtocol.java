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

package de.lars.remotelightcore.devices.arduino;

import de.lars.remotelightcore.utils.color.Color;

public class GlediatorProtocol {
	
	public static byte[] doOutput(Color[] leds) {
		
		int index = 0;
		
		byte[] outputBuffer = new byte[leds.length * 3 + 1];
		
		outputBuffer[index] = 1;
		index++;
		
		for(int i = 0; i < leds.length; i++) {
			Color tmp = leds[i];
			
			byte b1 = (byte) tmp.getRed();
			byte b2 = (byte) tmp.getGreen();
			byte b3 = (byte) tmp.getBlue();
			
			if(b1 == 1)
				b1 = 2;
			if(b2 == 1)
				b2 = 2;
			if(b3 == 1)
				b3 = 2;
			
			
			outputBuffer[index] = b1;
			outputBuffer[(index + 1)] = b2;
			outputBuffer[(index + 2)] = b3;
			
			index += 3;
		}
		
		return outputBuffer;
	}

}
