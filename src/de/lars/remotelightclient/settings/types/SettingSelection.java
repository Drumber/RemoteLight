package de.lars.remotelightclient.settings.types;

import de.lars.remotelightclient.settings.Setting;
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;

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
	}

	public String[] getValues() {
		return values;
	}

	public void setValues(String[] values) {
		this.values = values;
	}

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

}
