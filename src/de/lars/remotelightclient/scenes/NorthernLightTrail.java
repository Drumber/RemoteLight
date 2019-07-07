package de.lars.remotelightclient.scenes;

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
