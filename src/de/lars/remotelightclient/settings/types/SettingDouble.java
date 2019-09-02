package de.lars.remotelightclient.settings.types;

import de.lars.remotelightclient.settings.Setting;

public class SettingDouble extends Setting {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 238878694442965168L;
	private double value;
	private double min, max, stepsize;

	public SettingDouble(String id, String name, String description, double value, double min, double max, double stepsize) {
		super(id, name, description);
		this.value = value;
		this.min = min;
		this.max = max;
		this.stepsize = stepsize;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public double getStepsize() {
		return stepsize;
	}

	public void setStepsize(double stepsize) {
		this.stepsize = stepsize;
	}

}
