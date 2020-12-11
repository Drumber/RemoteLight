package de.lars.remotelightrestapi.handlers;

import com.google.gson.Gson;

import de.lars.remotelightcore.RemoteLightCore;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.router.RouterNanoHTTPD.DefaultHandler;

public class OutputHandler extends DefaultHandler {

	@Override
	public String getText() {
		return new Gson().toJson(RemoteLightCore.getInstance().getDeviceManager().getDevices());
	}

	@Override
	public IStatus getStatus() {
		return Response.Status.OK;
	}

	@Override
	public String getMimeType() {
		return "application/json";
	}

}
