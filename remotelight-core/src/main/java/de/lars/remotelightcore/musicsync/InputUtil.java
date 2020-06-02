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

package de.lars.remotelightcore.musicsync;

import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

public class InputUtil {
	
	public static boolean isLineSupported(Mixer mixer) {
        TargetDataLine line = null;
		try {
			mixer.open();
	        Line.Info linfo = new Line.Info(TargetDataLine.class);
	        try {
	            line = (TargetDataLine)mixer.getLine(linfo);
	            line.open();
	        } catch (IllegalArgumentException ex) {
	        	return false;
	        } catch (LineUnavailableException ex) {
	        	return true;
	        }
		} catch(LineUnavailableException | IllegalStateException e) {
			return true;
		} catch(Exception e) {
			return false;
		}
		mixer.close();
		line.close();
		return true;
	}

}
