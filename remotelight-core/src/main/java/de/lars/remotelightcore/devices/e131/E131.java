package de.lars.remotelightcore.devices.e131;

import java.awt.Color;

import de.lars.remotelightcore.devices.ConnectionState;
import de.lars.remotelightcore.devices.Device;

public class E131 extends Device {
	private static final long serialVersionUID = -4989081425851612020L;
	
	public final static int PORT = 5568;

	public E131(String id) {
		super(id, 0);
	}

	@Override
	public ConnectionState connect() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConnectionState disconnect() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConnectionState getConnectionState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void send(Color[] pixels) {
		// TODO Auto-generated method stub
		
	}

}
