package de.lars.remotelightclient.devices;

public class RaspberryPi extends Device {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 768993097355819403L;
	private String ip;

	public RaspberryPi(String id, String ip) {
		super(id);
		this.ip = ip;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

}
