package de.lars.remotelightcore.devices.e131;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.tinylog.Logger;

import de.lars.remotelightcore.devices.ConnectionState;
import de.lars.remotelightcore.devices.Device;
import de.lars.remotelightcore.utils.color.Color;

public class E131 extends Device {
	private static final long serialVersionUID = -4989081425851612020L;
	
	/* E1.31 Port is 5568 */
	public final static int PORT = 5568;
	/** Minimum universe size: 3 for a single pixel (RGB) */
	public final static int MIN_UNIVERSE_SIZE = 3;
	
	private transient int sequenceNumber;
	private int startUniverse;
	private int universeSize = E131Packet.DATA_LENGTH;
	
	private String unicastIP;
	private InetAddress address;
	private boolean multicast;
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
	
	public int getUniverseSize() {
		return universeSize;
	}

	public void setUniverseSize(int universeSize) {
		this.universeSize = universeSize;
	}

	protected void initializeSocket() {
		try {
			address = getAddress();
			dSocket = new DatagramSocket(null);
			dSocket.setReuseAddress(true);
			dSocket.setBroadcast(true);
			dSocket.bind(new InetSocketAddress((InetAddress) null, PORT));
		} catch (UnknownHostException | SocketException e) {
			dSocket = null;
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
		if(universeSize == 0) {
			universeSize = E131Packet.DATA_LENGTH;
		}
		if(universeSize < MIN_UNIVERSE_SIZE) {
			universeSize = MIN_UNIVERSE_SIZE;
		}
	}

	@Override
	public ConnectionState connect() {
		if(e131Packet == null) onLoad();
		initializeSocket();
		return (dSocket != null) ? ConnectionState.CONNECTED : ConnectionState.FAILED;
	}

	@Override
	public ConnectionState disconnect() {
		dSocket.close();
		dSocket = null;
		return ConnectionState.DISCONNECTED;
	}

	@Override
	public ConnectionState getConnectionState() {
		return (dSocket != null) ? ConnectionState.CONNECTED : ConnectionState.DISCONNECTED;
	}

	@Override
	public void send(Color[] pixels) {
		final int dataLength = pixels.length * 3;
		final int MAX_LENGTH = universeSize; // maximal dmx data length
		
		int arrayLength = dataLength > MAX_LENGTH ? MAX_LENGTH : dataLength;
		byte[] dmxData = new byte[arrayLength];
		int currUniverse = startUniverse;
		int offset = 0;
		
		// loop over each pixel
		for(int i = 0; i < pixels.length; i++) {
			byte[] rgbData = {(byte) pixels[i].getRed(), (byte) pixels[i].getGreen(), (byte) pixels[i].getBlue()};
			
			// loop over RGB data array
			for(int d = 0; d < rgbData.length; d++) {
				// check if max length of universe is reached
				if(offset >= MAX_LENGTH) {
					// current universe is full; output universe and use next universe
					sendDmxData(currUniverse, dmxData);
					
					// use next universe
					currUniverse++;
					// reset offset
					offset = 0;
					
					// create new buffer
					int usedUniverses = currUniverse - startUniverse;
					int remainingDataLength = dataLength - usedUniverses * MAX_LENGTH;
					int size = remainingDataLength > MAX_LENGTH ? MAX_LENGTH : remainingDataLength;
					dmxData = new byte[size];
				}
				
				// add to output data buffer
				dmxData[offset] = rgbData[d];
				// increment offset
				offset++;
			}
		}
		// output universe
		sendDmxData(currUniverse, dmxData);
	}
	
	protected void sendDmxData(int universe, byte[] colorData) {
		byte[] packetData;
		synchronized (e131Packet) {
			packetData = e131Packet.createPacket(universe, sequenceNumber, colorData);
		}
		incrementSequenceNumber();
		
		if(getConnectionState() == ConnectionState.CONNECTED) {
			// construct new DatagramPacket
			DatagramPacket dPacket = new DatagramPacket(packetData, packetData.length, address, PORT);
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
	
	public int getEndUniverse(int startUniverse, int universeSize, int pixels) {
		return startUniverse + (3 * pixels / universeSize);
	}
	
}
