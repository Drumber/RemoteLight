package de.lars.remotelightrestapi.config;

public class WebserverConfiguration {

	private String baseUrl = "http://localhost/";
	private int port = 8080;
	
	public WebserverConfiguration(String baseUrl, int port) {
		this.baseUrl = baseUrl;
		this.port = port;
	}
	
	public WebserverConfiguration() {
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
