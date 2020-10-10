package de.lars.remotelightcore.devices.e131;

import java.awt.Color;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.tinylog.Logger;

import de.lars.remotelightcore.devices.ConnectionState;
import de.lars.remotelightcore.devices.Device;

public class E131 extends Device {
	private static final long serialVersionUID = -4989081425851612020L;
	
	public final static int PORT = 5568;
	
	private transient int sequenceNumber;
	private int startUniverse;
	
	private String unicastIP;
	private boolean multicast;
	private transient DatagramPacket dPacket;
	private transient DatagramSocket dSocket;
	private transient E131Packet e131Packet;

	public E131(String id) {
		super(id, 0);
	}
	
	public void setMulticast(boolean enabled) {
		multicast = enabled;
	}
	
	public boolean isMulticastMode() {
		return multicast;
	}
	
	public void setUnicastAddress(String unicastIP) {
		this.unicastIP = unicastIP;
	}
	
	public String getUnicastAddress() {
		return unicastIP;
	}
	
	public void setStartUniverse(int startUniverse) {
		this.startUniverse = startUniverse;
	}
	
	public int getStartUniverse() {
		return startUniverse;
	}
	
	protected void initializeSocket() {
		InetAddress address;
		try {
			address = getAddress();
			dPacket = new DatagramPacket(new byte[0], 0, address, PORT);
			dSocket = new DatagramSocket();
		} catch (UnknownHostException | SocketException e) {
			Logger.error(e, "Could not initialize E1.31 client!");
		}
	}
	
	protected InetAddress getAddress() throws UnknownHostException {
		if(!multicast) {
			return InetAddress.getByName(unicastIP);
		}
		// multicast address must be 239.255.UHB.ULB (UHB = universe high byte, ULB = universe low byte)
		byte[] addressBytes = {(byte) 239, (byte) 255, (byte) (startUniverse >> 8), (byte) (startUniverse % 255)};
		return InetAddress.getByAddress(addressBytes);
	}
	
	@Override
	public void onLoad() {
		e131Packet = new E131Packet();
	}

	@Override
	public ConnectionState connect() {
		if(e131Packet == null) onLoad();
		initializeSocket();
		return (dPacket != null && dSocket != null) ? ConnectionState.CONNECTED : ConnectionState.FAILED;
	}

	@Override
	public ConnectionState disconnect() {
		dSocket.close();
		dPacket = null;
		dPacket = null;
		return ConnectionState.DISCONNECTED;
	}

	@Override
	public ConnectionState getConnectionState() {
		return (dPacket != null && dSocket != null) ? ConnectionState.CONNECTED : ConnectionState.DISCONNECTED;
	}

	@Override
	public void send(Color[] pixels) {
		final int dataLength = pixels.length * 3;
		final int MAX_LENGTH = E131Packet.DATA_LENGTH; // maximal dmx data length
		
		int arrayLength = dataLength > MAX_LENGTH ? MAX_LENGTH : dataLength;
		byte[] dmxData = new byte[arrayLength];
		int currUniverse = startUniverse;
		
		for(int i = 0; i < pixels.length; i++) {
			byte[] rgbData = {(byte) pixels[i].getRed(), (byte) pixels[i].getGreen(), (byte) pixels[i].getBlue()};
			
			for(int d = 0; d < rgbData.length; d++) {
				int additionalUniverses = currUniverse - startUniverse;
				int index = (MAX_LENGTH * (currUniverse - startUniverse - additionalUniverses)) + (i * 3 - MAX_LENGTH * additionalUniverses);
				
				if(index + d >= MAX_LENGTH) {
					// current universe is full; output universe and use next universe
					sendDmxData(currUniverse, dmxData);
					
					// use next universe
					currUniverse++;
					index = 0 - d;
					
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
		byte[] packetData = e131Packet.createPacket(universe, sequenceNumber, colorData);
		incrementSequenceNumber();
		
		if(getConnectionState() == ConnectionState.CONNECTED) {
			// set the new packet data
			dPacket.setData(packetData);
			// send the packet
			try {
				dSocket.send(dPacket);
			} catch (IOException e) {
				Logger.error(e, "Could not send E1.31 data.");
				disconnect();
			}
		}
	}
	
	protected void incrementSequenceNumber() {
		if(++sequenceNumber > 255)
			sequenceNumber = 0;
	}
	
	public int getEndUniverse(int startUniverse, int pixels) {
		return startUniverse + (3 * pixels / 512);
	}
	
}
