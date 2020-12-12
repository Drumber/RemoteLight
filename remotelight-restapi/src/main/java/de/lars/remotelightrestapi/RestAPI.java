package de.lars.remotelightrestapi;

import java.io.IOException;
import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.lars.remotelightrestapi.config.WebserverConfiguration;
import de.lars.remotelightrestapi.handlers.EffectsHandler;
import de.lars.remotelightrestapi.handlers.EffectsHandler.EffectsActiveHandler;
import de.lars.remotelightrestapi.handlers.OutputsHandler;
import de.lars.remotelightrestapi.handlers.OutputsHandler.OutputActivateHandler;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;

public class RestAPI extends RouterNanoHTTPD {

	private static Gson gson;
	public static boolean shouldLog = true;
	
	private WebserverConfiguration serverConfig;

	public RestAPI(WebserverConfiguration serverConfig) throws IOException {
		super(serverConfig.getPort());
		this.serverConfig = serverConfig;
		addNanoHttpdLogFilter();
		addMappings();
		start(NanoHTTPD.SOCKET_READ_TIMEOUT, true);
	}

	@Override
	public void addMappings() {
		setNotImplementedHandler(NotImplementedHandler.class);
        setNotFoundHandler(Error404UriHandler.class);
        // outputs
        addRoute("/outputs", OutputsHandler.class);
		addRoute("/outputs/active", OutputActivateHandler.class);
		// effects
		addRoute("/effects", EffectsHandler.class);
		addRoute("/effects/:type/active", EffectsActiveHandler.class);
		addRoute("/effects/:type", EffectsHandler.class);
	}

	
	// filters 'SocketException: Could not send response to the client'
	private void addNanoHttpdLogFilter() {
		Logger.getLogger(NanoHTTPD.class.getName()).setFilter(new Filter() {
			@Override
			public boolean isLoggable(LogRecord record) {
				return !"Could not send response to the client".equals(record.getMessage());
			}
		});
	}
	
	/**
	 * Get the shared Gson instance.
	 * @return	shared Gson instance
	 */
	public static Gson getGson() {
		if(gson == null) {
			gson = new GsonBuilder()
					.serializeNulls()
					.setPrettyPrinting()
					.create();
		}
		return gson;
	}

}
