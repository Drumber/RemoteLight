package de.lars.remotelightclient.settings.types;

import de.lars.remotelightclient.settings.Setting;
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;

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
		this.value = value;
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
