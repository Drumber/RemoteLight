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

package de.lars.remotelightcore.settings.types;

import de.lars.remotelightcore.settings.Setting;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;

public class SettingInt extends Setting {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6084760644268375263L;
	private int value;
	private int min, max, stepsize;

	public SettingInt(String id, String name, SettingCategory category, String description, int value, int min, int max, int stepsize) {
		super(id, name, description, category);
		this.value = value;
		this.min = min;
		this.max = max;
		this.stepsize = stepsize;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		boolean change = value != this.value;
		this.value = value;
		if(change)
			fireChangeEvent();
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getStepsize() {
		return stepsize;
	}

	public void setStepsize(int stepsize) {
		this.stepsize = stepsize;
	}

}
