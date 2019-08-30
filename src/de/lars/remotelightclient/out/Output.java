package de.lars.remotelightclient.out;

import java.awt.Color;
import java.io.Serializable;

public abstract class Output implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4585718970709898453L;
	private String id;
	
	public Output(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public abstract void onOutput(Color[] pixels);

}
