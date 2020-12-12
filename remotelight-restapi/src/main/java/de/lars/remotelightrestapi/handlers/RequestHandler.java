package de.lars.remotelightrestapi.handlers;

import java.io.InputStream;
import java.util.Map;

import de.lars.remotelightrestapi.RestAPI;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.router.RouterNanoHTTPD.DefaultStreamHandler;
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource;

public abstract class RequestHandler extends DefaultStreamHandler {

	@Override
	public abstract IStatus getStatus();

	@Override
	public String getMimeType() {
		return "application/json";
	}
	
	@Override
	public abstract Response get(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session);
	
	protected Response text(String text) {
		return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), text);
	}
	
	protected Response text(String text, IStatus status) {
		Response response = text(text);
		response.setStatus(status);
		return response;
	}
	
	protected Response json(Object src) {
		return text(RestAPI.getGson().toJson(src));
	}

	@Override
	public InputStream getData() {
		throw new IllegalStateException("Operation is not supported by this handler.");
	}

}
