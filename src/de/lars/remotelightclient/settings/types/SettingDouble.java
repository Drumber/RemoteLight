/*******************************************************************************
 * ______                     _       _     _       _     _   
 * | ___ \                   | |     | |   (_)     | |   | |  
 * | |_/ /___ _ __ ___   ___ | |_ ___| |    _  __ _| |__ | |_ 
 * |    // _ \ '_ ` _ \ / _ \| __/ _ \ |   | |/ _` | '_ \| __|
 * | |\ \  __/ | | | | | (_) | ||  __/ |___| | (_| | | | | |_ 
 * \_| \_\___|_| |_| |_|\___/ \__\___\_____/_|\__, |_| |_|\__|
 *                                             __/ |          
 *                                            |___/           
 * 
 * Copyright (C) 2019 Lars O.
 * 
 * This file is part of RemoteLight.
 ******************************************************************************/
package de.lars.remotelightclient.settings.types;

import de.lars.remotelightclient.settings.Setting;
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;

public class SettingDouble extends Setting {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 238878694442965168L;
	private double value;
	private double min, max, stepsize;

	public SettingDouble(String id, String name, SettingCategory category, String description, double value, double min, double max, double stepsize) {
		super(id, name, description, category);
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
