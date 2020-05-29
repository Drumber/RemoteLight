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
package de.lars.remotelightcore.devices;

import java.awt.Color;
import java.io.Serializable;

import de.lars.remotelightcore.devices.arduino.RgbOrder;
import de.lars.remotelightcore.out.Output;

public abstract class Device extends Output implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5542594482384646241L;
	private RgbOrder rgbOrder;
	
	/**
	 * @param id User defined name for the device
	 */
	public Device(String id, int pixels) {
		super(id, pixels);
		rgbOrder = RgbOrder.RGB;
	}
	
	public abstract ConnectionState connect();
	public abstract ConnectionState disconnect();
	public abstract ConnectionState getConnectionState();
	/**
	 * Called when Device is loaded from data file
	 */
	public abstract void onLoad();
	
	public abstract void send(Color[] pixels);
	
	@Override
	public void onOutput(Color[] pixels) {
		pixels = getOutputPatch().patchOutput(pixels, getRgbOrder());
		send(pixels);
	}
	
	public RgbOrder getRgbOrder() {
		return rgbOrder;
	}

	public void setRgbOrder(RgbOrder rgbOrder) {
		this.rgbOrder = rgbOrder;
	}

	@Override
	public ConnectionState getState() {
		return getConnectionState();
	}
	
	@Override
	public void onActivate() {
		connect();
		super.onActivate();
	}
	
	@Override
	public void onDeactivate() {
		disconnect();
		super.onDeactivate();
	}

}
