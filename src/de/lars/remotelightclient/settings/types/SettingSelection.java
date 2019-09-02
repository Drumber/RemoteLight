package de.lars.remotelightclient.settings.types;

import de.lars.remotelightclient.settings.Setting;

public class SettingSelection extends Setting {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -326278354294604364L;
	private String[] values;
	private Model model;
	public enum Model {
		RadioButton, ComboBox
	}

	public SettingSelection(String id, String name, String description, String[] values, Model model) {
		super(id, name, description);
		this.values = values;
		this.model = model;
	}

	public String[] getValues() {
		return values;
	}

	public void setValues(String[] values) {
		this.values = values;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

}
