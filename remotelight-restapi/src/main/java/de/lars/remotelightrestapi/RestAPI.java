package de.lars.remotelightrestapi;

import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.lars.remotelightrestapi.handlers.ColorHandler;
import de.lars.remotelightrestapi.handlers.ColorHandler.ColorHandlerPixels;
import de.lars.remotelightrestapi.handlers.EffectsHandler;
import de.lars.remotelightrestapi.handlers.EffectsHandler.EffectsActiveHandler;
import de.lars.remotelightrestapi.handlers.InformationHandler;
import de.lars.remotelightrestapi.handlers.OutputsHandler;
import de.lars.remotelightrestapi.handlers.OutputsHandler.OutputActivateHandler;
import de.lars.remotelightrestapi.handlers.SettingsHandler;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;

public class RestAPI extends RouterNanoHTTPD {

	private static RestAPI instance;
	private static Gson gson;
	public static boolean shouldLog = true;
	
	public RestAPI(final int port) {
		super(port);
		instance = this;
		addNanoHttpdLogFilter();
		addMappings();
	}

	@Override
	public void addMappings() {
		setNotImplementedHandler(NotImplementedHandler.class);
        setNotFoundHandler(Error404UriHandler.class);
        // information
     	addRoute("/", InformationHandler.class);
     	// outputs
        addRoute("/outputs", OutputsHandler.class);
		addRoute("/outputs/active", OutputActivateHandler.class);
		// effects
		addRoute("/effects", EffectsHandler.class);
		addRoute("/effects/:type/active", EffectsActiveHandler.class);
		addRoute("/effects/:type", EffectsHandler.class);
		// color
		addRoute("/color", ColorHandler.class);
		addRoute("/color/pixel", ColorHandlerPixels.class);
		// settings
		addRoute("/settings", SettingsHandler.class);
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
	
	public static RestAPI newInstance(final int port) {
		if(getInstance() != null) {
			getInstance().stop();
		}
		return new RestAPI(port);
	}
	
	public static RestAPI getInstance() {
		return instance;
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
