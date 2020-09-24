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
import java.util.HashSet;
import java.util.Set;

import de.lars.remotelightcore.lang.i18n;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;

public abstract class Setting implements Serializable {
	private static final long serialVersionUID = -7324328394259004155L;
	
	// default flags
	public transient static final String HIDDEN = "hidden";
	
	private String name;
	private String id;
	private String description;
	private SettingCategory category;
	private transient SettingValueListener listener;
	private transient Set<String> flags;
	
	public Setting(String id, String name, String description, SettingCategory category) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.category = category;
		initFlagsSet();
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
	
	/**
	 * Get the stored setting value of this setting instance
	 * @return		stored setting value
	 */
	public abstract Object get();
	
	/**
	 * Add a setting flag
	 * @param flag	the flag to add
	 * @return		true if the flag was not already added
	 */
	public boolean addFlag(String flag) {
		initFlagsSet();
		return flags.add(flag);
	}
	
	/**
	 * Remove a setting flag
	 * @param flag	the flag to remove
	 * @return		true if the flag was present in the set
	 */
	public boolean removeFlag(String flag) {
		initFlagsSet();
		return flags.remove(flag);
	}
	
	/**
	 * Check if this setting instance contains the flag
	 * @param flag	the flag to check for
	 * @return		true if this settings contains the flag
	 */
	public boolean hasFlag(String flag) {
		initFlagsSet();
		return flags.contains(flag);
	}
	
	public Set<String> getFlags() {
		initFlagsSet();
		return flags;
	}
	
	private void initFlagsSet() {
		if(flags == null)
			flags = new HashSet<String>();
	}
	
	public void setValueListener(SettingValueListener listener) {
		this.listener = listener;
	}
	
	protected void fireChangeEvent() {
		if(listener != null)
			listener.onSettingValueChanged(this);
	}

}
