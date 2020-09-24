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

public class SettingBoolean extends Setting {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4432151117700184493L;
	private boolean value;

	public SettingBoolean(String id, String name, SettingCategory category, String description, boolean value) {
		super(id, name, description, category);
		this.value = value;
	}
	
	@Override
	public Boolean getValue() {
		return value;
	}
	
	public void setValue(boolean value) {
		boolean change = value != this.value;
		this.value = value;
		if(change)
			fireChangeEvent();
	}

}
