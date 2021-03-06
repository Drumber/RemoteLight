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

public class SettingObject extends Setting {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7792796690437700794L;
	private Object value;

	/**
	 * (!) This Setting will not be visible in settings menu
	 */
	public SettingObject(String id, String name, Object value) {
		super(id, name, null, SettingCategory.Intern);
		this.value = value;
	}

	/**
	 * Get the object value
	 * @return			stored object value
	 * @deprecated		will be removed in future versions,
	 * 					use {@link #get()} instead
	 */
	@Deprecated
	public Object getValue() {
		return value;
	}
	
	@Override
	public Object get() {
		return value;
	}
	
	@Override
	public void set(Object o) {
		setValue(o);
	}

	public void setValue(Object value) {
		this.value = value;
		fireChangeEvent();
	}

}
