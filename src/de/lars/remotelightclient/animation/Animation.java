package de.lars.remotelightclient.animation;

public class Animation {
	
	private String name;
	private String displayname;
	private int delay;
	private boolean adjustable;
	
	/**
	 * Animation with adjustable speed
	 */
	public Animation(String name) {
		this.name = name;
		this.displayname = name; //TODO Language system
		adjustable = true;
	}
	
	/**
	 * Animation with pre-defined speed (not adjustable)
	 */
	public Animation(String name, int delay) {
		this.name = name;
		this.displayname = name; //TODO Language system
		this.delay = delay;
		adjustable = false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayname() {
		return displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}
	
	public boolean isAdjustable() {
		return this.adjustable;
	}
	
	public int getDelay() {
		return this.delay;
	}
	
	public void onEnable() {}
	public void onDisable() {}
	public void onLoop() {}

}
