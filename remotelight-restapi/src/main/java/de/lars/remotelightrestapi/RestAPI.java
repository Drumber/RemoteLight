package de.lars.remotelightrestapi;

import java.io.IOException;

import de.lars.remotelightrestapi.config.WebserverConfiguration;
import de.lars.remotelightrestapi.handlers.OutputHandler;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;

public class RestAPI extends RouterNanoHTTPD {
	
	private WebserverConfiguration serverConfig;
	
	public RestAPI(WebserverConfiguration serverConfig) throws IOException {
		super(serverConfig.getPort());
		this.serverConfig = serverConfig;
		addMappings();
		start(NanoHTTPD.SOCKET_READ_TIMEOUT, true);
	}
	
	@Override
	public void addMappings() {
		super.addMappings();
		addRoute("/output", OutputHandler.class);
	}

}
