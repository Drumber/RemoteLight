package de.lars.remotelightclient.scene;

public class Scene {
	
	private String name;
	private String displayname;
	private int delay;
	
	public Scene(String name, int delay) {
		this.name = name;
		this.displayname = name; //TODO Language system
		this.delay = delay;
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
	
	public int getDelay() {
		return delay;
	}
	
	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	public void onEnable() {}
	public void onDisable() {}
	public void onLoop() {}

}
