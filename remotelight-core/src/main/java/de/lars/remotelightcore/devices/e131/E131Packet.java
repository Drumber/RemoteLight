package de.lars.remotelightcore.devices.e131;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class E131Packet {
	
	/** max E1.31 packet length */
	public final static int LENGTH = 638;
	/** max DMX data length */
	public final static int DATA_LENGTH = 512;
	/** Sender's unique ID generated using current user name */
	public final static UUID CLIENT_UUID = UUID.nameUUIDFromBytes(new String("RemoteLight_" + System.getProperty("user.name")).getBytes(StandardCharsets.UTF_8));
	
	private ByteBuffer buffer;
	
	public byte[] createPacket(int universe, int sequenceId, byte[] colorData) {
		if(colorData.length > DATA_LENGTH)
			throw new IllegalStateException("Maximum DMX data length exceeded: " + colorData.length + " (max " + DATA_LENGTH + ")");
		
		if(buffer == null) {
			buffer = ByteBuffer
					.allocate(LENGTH)
					.order(ByteOrder.BIG_ENDIAN);
		} else {
			buffer.clear();
		}
		
		/*
		 * Create packet layer instances
		 */
		DmpLayer dmpLayer = new DmpLayer();
		FramingLayer framingLayer = new FramingLayer(universe, sequenceId);
		RootLayer rootLayer = new RootLayer();
		
		/*
		 * Update Packet lengths
		 */
		int slots = colorData.length;
		
		// Device Management Protocol Layer
		dmpLayer.slotsNumber = 1 + slots;
		dmpLayer.length = 10 + 1 + slots;
		// Framing Layer
		framingLayer.length = 77 + dmpLayer.length;
		// Root Layer
		rootLayer.length = 22 + framingLayer.length;
		
		/*
		 * Assemble Layers (put data in buffer)
		 */
		rootLayer.putData();
		framingLayer.putData();
		dmpLayer.putData(colorData);
		
		/*
		 * Clone byte array and Return packet
		 */
		return buffer.array().clone();
	}
	
	
	class RootLayer {
		
		int length = LENGTH - 16; // 22 + frameLayer length (22 + 600 for full payload)
		
		void putData() {
			buffer.putShort((short) 0x0010);			// Preamble Size (2 bytes)
			buffer.putShort((short) 0x0000);			// Post-amble Size (2 bytes)
														// ACN Packet Identifier (12 bytes)
			final byte[] ACN_PID = {0x41, 0x53, 0x43, 0x2d, 0x45, 0x31, 0x2e, 0x31, 0x37, 0x00, 0x00, 0x00};
			buffer.put(ACN_PID, 4, ACN_PID.length);
			
			buffer.putShort((short) (0x7000 | length));	// Flags & Length (2 bytes) (low 12 bits = PDU length, high 4 bits = 0x7)
			buffer.putInt(0x00000004);					// Vector (4 bytes)
														// CID (16 bytes = 8 bytes + 8 bytes)
			buffer.putLong(CLIENT_UUID.getMostSignificantBits());
			buffer.putLong(CLIENT_UUID.getLeastSignificantBits());
		}
		
	}
	
	class FramingLayer {
		
		int length = LENGTH - 38; // frameLayer length + dmpLayer length (600 for full payload)
		int universe;
		int sequenceId;
		
		public FramingLayer(int universe, int sequenceId) {
			this.universe = universe;
			this.sequenceId = sequenceId;
		}
		
		void putData() {
			buffer.putShort((short) (0x7000 | length));	// Flags & Length (2 bytes) (low 12 bits = PDU length, high 4 bits = 0x7)
			buffer.putInt(0x00000002);					// Vector (4 bytes)
														// Source Name (64 bytes) [UTF-8]
			final byte[] SOURCE_NAME = new String("RemoteLight").getBytes(StandardCharsets.UTF_8); // 11 bytes
			buffer.put(SOURCE_NAME, 44, SOURCE_NAME.length);
			buffer.position(108); // set position to octet 108
			
			buffer.put((byte) 100);						// Priority (1 byte) [0-200]
			buffer.putShort((short) 0);					// Synchronization Address (2 bytes)
			buffer.put((byte) sequenceId);				// Sequence Number (1 byte)
			buffer.put((byte) 0);						// Options Flags (1 byte)
			buffer.putShort((short) universe);			// Universe Number (2 bytes)
		}
		
	}
	
	class DmpLayer {
		
		int length = LENGTH - 115; // dmpLayer length (523 for full payload)
		int slotsNumber = 0x0201;
		
		void putData(byte[] colorData) {
			buffer.putShort((short) (0x7000 | length));	// Flags & Length (2 bytes) (low 12 bits = PDU length, high 4 bits = 0x7)
			buffer.put((byte) 0x02);					// Vector (1 byte)
			buffer.put((byte) 0xa1);					// Address Type & Data Type (1 byte)
			buffer.putShort((short) 0x0000);			// First Property Address (2 bytes)
			buffer.putShort((short) 0x0001);			// Address Increment (2 bytes)
			buffer.putShort((short) slotsNumber);		// Property value count (2 bytes)
			buffer.put((byte) 0x00);					// DMX512-A START Code (1-512 bytes)
			// put color data into the buffer (should be starting at position 126)
			buffer.put(colorData, buffer.position(), colorData.length);
		}
		
	}

}
