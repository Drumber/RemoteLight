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

import java.util.Arrays;

import de.lars.remotelightcore.settings.Setting;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;

public class SettingSelection extends Setting {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -326278354294604364L;
	private String[] values;
	private String selected;
	private Model model;
	public enum Model {
		RadioButton, ComboBox
	}

	public SettingSelection(String id, String name, SettingCategory category, String description, String[] values, String selected, Model model) {
		super(id, name, description, category);
		this.values = values;
		this.model = model;
		this.selected = selected;
	}

	public String[] getValues() {
		return values;
	}

	public void setValues(String[] values) {
		this.values = values;
	}

	@Override
	public String getValue() {
		return getSelected();
	}
	
	public String getSelected() {
		return selected;
	}
	
	public int getSelectedIndex() {
		return Arrays.asList(values).indexOf(selected);
	}

	public void setSelected(String selected) {
		boolean change = !selected.equals(this.selected);
		this.selected = selected;
		if(change)
			fireChangeEvent();
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

}
