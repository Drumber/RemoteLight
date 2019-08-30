package de.lars.remotelightclient.devices;

import java.io.Serializable;

public class Device implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5542594482384646241L;
	private String id;
	private boolean enabled;
	
	/**
	 * @param id User defined name for the device
	 */
	public Device(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
