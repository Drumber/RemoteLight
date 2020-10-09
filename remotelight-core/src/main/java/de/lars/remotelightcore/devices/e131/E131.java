package de.lars.remotelightcore.devices.e131;

import java.awt.Color;

import de.lars.remotelightcore.devices.ConnectionState;
import de.lars.remotelightcore.devices.Device;

public class E131 extends Device {
	private static final long serialVersionUID = -4989081425851612020L;
	
	public final static int PORT = 5568;
	
	private transient int sequenceNumber;
	private int startUniverse;

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
		final int dataLength = pixels.length * 3;
		final int MAX_LENGTH = E131Packet.DATA_LENGTH; // maximal dmx data length
		
		byte[] dmxData = new byte[dataLength % MAX_LENGTH];
		int currUniverse = startUniverse;
		
		for(int i = 0; i < pixels.length; i++) {
			byte[] rgbData = {(byte) pixels[i].getRed(), (byte) pixels[i].getGreen(), (byte) pixels[i].getBlue()};
			
			for(int d = 0; d < rgbData.length; d++) {
				int index = (MAX_LENGTH * (currUniverse - startUniverse)) + i * 3;
				
				if(index >= MAX_LENGTH) {
					// current universe is full; output universe and use next universe
					sendDmxData(currUniverse, dmxData);
					
					// use next universe
					currUniverse++;
					index = 0;
					
					// create new buffer
					int size = (dataLength - (currUniverse - startUniverse) * MAX_LENGTH) % MAX_LENGTH;
					dmxData = new byte[size];
				}
				// add to output data buffer
				dmxData[index + d] = rgbData[d];
			}
		}
		// output universe
		sendDmxData(currUniverse, dmxData);
	}
	
	protected void sendDmxData(int universe, byte[] colorData) {
		E131Packet packet = new E131Packet();
		byte[] packetData = packet.createPacket(universe, sequenceNumber, colorData);
		incrementSequenceNumber();
		// TODO: send packet via client
	}
	
	protected void incrementSequenceNumber() {
		if(++sequenceNumber > 255)
			sequenceNumber = 0;
	}

}
