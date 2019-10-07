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
package de.lars.remotelightclient.ui.panels.output.outputComps;

import javax.swing.JPanel;

import de.lars.remotelightclient.devices.Device;

public abstract class DeviceSettingsPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1279445549682121981L;
	private Device device;
	private boolean setup;

	/**
	 * Create the panel.
	 */
	public DeviceSettingsPanel(Device device, boolean setup) {
		this.device = device;
		this.setup = setup;
	}
	
	public Device getDevice() {
		return device;
	}

	public boolean isSetup() {
		return setup;
	}

	/**
	 * 
	 * @return returns false if ID is empty
	 */
	public abstract boolean save();
	
	public abstract String getId();

}
