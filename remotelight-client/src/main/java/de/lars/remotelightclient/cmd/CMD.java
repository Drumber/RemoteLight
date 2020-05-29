package de.lars.remotelightclient.cmd;

public enum CMD {
	
	START("start"),
	STOP("stop"),
	LIST("list"),
	CLOSE("close");
	
	private final String text;
	
	CMD(final String text) {
		this.text = text;
	}
	
	@Override
	public String toString() {
		return text;
	}

}
