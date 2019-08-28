package de.lars.remotelightclient.animation;

public class Animation {
	
	private String name;
	private String displayname;
	private boolean enabled;
	private int delay;
	private boolean adjustable;
	
	/**
	 * Animation with adjustable speed
	 */
	public Animation(String name, String displayname) {
		this.name = name;
		this.displayname = displayname;
		adjustable = true;
	}
	
	/**
	 * Animation with pre-defined speed (not adjustable)
	 */
	public Animation(String name, String displayname, int delay) {
		this.name = name;
		this.displayname = displayname;
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
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean b) {
		enabled = b;
		if(enabled) {
			this.onEnable();
		} else {
			this.onDisable();
		}
	}
	
	public void toggle() {
		this.setEnabled(!enabled);
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
