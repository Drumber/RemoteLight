package de.lars.remotelightcore;

public abstract class AbstractEffect {
	
	private String name;
	private String displayname;
	
	public AbstractEffect(String name) {
		this.name = name;
		this.displayname = name;
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
	
	/**
	 * Get the amount of LEDs/pixels
	 * 
	 * @return	the amount of LEDs
	 */
	public int getLeds() {
		return RemoteLightCore.getLedNum();
	}
	
	public void onEnable() {}
	public void onDisable() {}
	public void onLoop() {}

}
