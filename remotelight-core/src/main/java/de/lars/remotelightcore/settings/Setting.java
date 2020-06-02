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

package de.lars.remotelightcore.settings;

import java.io.Serializable;

import de.lars.remotelightcore.lang.i18n;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;

public class Setting implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7324328394259004155L;
	private String name;
	private String id;
	private String description;
	private SettingCategory category;
	
	public Setting(String id, String name, String description, SettingCategory category) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.category = category;
		if(name == null) this.name = "";
		if(description == null) this.description = "";
	}

	public String getName() {
		if(i18n.getSettingString(id + ".name") != null)
			return i18n.getSettingString(id + ".name");
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		if(i18n.getSettingString(id + ".description") != null)
			return i18n.getSettingString(id + ".description");
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public SettingCategory getCategory() {
		return category;
	}

	public void setCategory(SettingCategory category) {
		this.category = category;
	}

}
