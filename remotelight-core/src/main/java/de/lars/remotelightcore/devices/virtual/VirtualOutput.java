package de.lars.remotelightcore.devices.virtual;

import java.awt.Color;

import de.lars.remotelightcore.devices.ConnectionState;
import de.lars.remotelightcore.devices.Device;

public class VirtualOutput extends Device {
	private static final long serialVersionUID = -6877178657792878944L;
	
	private transient PixelOutputStream out;

	public VirtualOutput(String id, int pixels) {
		super(id, pixels);
		out = new PixelOutputStream();
	}

	@Override
	public ConnectionState connect() {
		out.setActive(true);
		return ConnectionState.CONNECTED;
	}

	@Override
	public ConnectionState disconnect() {
		out.setActive(false);
		return ConnectionState.DISCONNECTED;
	}

	@Override
	public ConnectionState getConnectionState() {
		return out.isActive() ? ConnectionState.CONNECTED : ConnectionState.DISCONNECTED;
	}

	@Override
	public void onLoad() {
		if(out == null)
			out = new PixelOutputStream();
	}

	@Override
	public void send(Color[] pixels) {
		out.writeStrip(pixels);
	}

}
