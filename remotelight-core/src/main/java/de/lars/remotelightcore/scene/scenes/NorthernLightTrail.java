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

package de.lars.remotelightcore.scene.scenes;

import de.lars.remotelightcore.utils.color.Color;
import java.io.Serializable;

public class NorthernLightTrail implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7916092332648803357L;
	public int startLed;
	public Color color;
	public boolean right;
	
	public NorthernLightTrail(int startLed, Color color, boolean right) {
		this.startLed = startLed;
		this.color = color;
		this.right = right;
	}

}
