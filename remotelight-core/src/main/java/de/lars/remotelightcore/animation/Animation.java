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

package de.lars.remotelightcore.animation;

import de.lars.remotelightcore.Effect;

public class Animation extends Effect {
	
	private int delay;
	private boolean adjustable;
	
	/**
	 * Animation with adjustable speed
	 * 
	 * @param name	the name of the animation (should be unique)
	 */
	public Animation(String name) {
		super(name);
		adjustable = true;
	}
	
	/**
	 * Animation with pre-defined speed (not adjustable)
	 * 
	 * @param name	the name of the animation (should be unique)
	 * @param delay	the pre-defined speed/delay in milliseconds
	 */
	public Animation(String name, int delay) {
		super(name);
		this.delay = delay;
		adjustable = false;
	}
	
	public boolean isAdjustable() {
		return this.adjustable;
	}
	
	public int getDelay() {
		return this.delay;
	}

}
