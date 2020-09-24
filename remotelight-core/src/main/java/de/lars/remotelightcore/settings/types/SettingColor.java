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

import java.awt.Color;

import de.lars.remotelightcore.settings.Setting;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;

public class SettingColor extends Setting {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8427251884477182754L;
	private Color value;

	public SettingColor(String id, String name, SettingCategory category, String description, Color value) {
		super(id, name, description, category);
		this.value = value;
	}

	/**
	 * Get the color value
	 * @return			stored color value
	 * @deprecated		will be removed in future versions,
	 * 					use {@link #get()} instead
	 */
	@Deprecated
	public Color getValue() {
		return value;
	}
	
	@Override
	public Color get() {
		return value;
	}

	public void setValue(Color value) {
		boolean change = value.getRGB() != this.value.getRGB();
		this.value = value;
		if(change)
			fireChangeEvent();
	}

}
