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
package de.lars.remotelightclient.scene.scenes;

import java.awt.Color;
import java.io.Serializable;

public class NorthernLightTrail implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7916092332648803357L;
	public int startLed;
	public Color color;
	public boolean right;
	
	public NorthernLightTrail(int startLed, Color color, boolean right) {
		this.startLed = startLed;
		this.color = color;
		this.right = right;
	}

}
