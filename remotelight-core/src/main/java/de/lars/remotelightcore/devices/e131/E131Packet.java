package de.lars.remotelightcore.devices.e131;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class E131Packet {
	
	public final static int LENGTH = 638;
	/** Sender's unique ID generated using current user name */
	public final static UUID CLIENT_UUID = UUID.nameUUIDFromBytes(new String("RemoteLight_" + System.getProperty("user.name")).getBytes(StandardCharsets.UTF_8));
	
	private ByteBuffer buffer;
	
	public byte[] getPacket(int universe) {
		buffer = ByteBuffer
				.allocate(LENGTH)
				.order(ByteOrder.BIG_ENDIAN);
		
		FramingLayer framingLayer = new FramingLayer(universe);
		RootLayer rootLayer = new RootLayer();
		
		rootLayer.putData();
		framingLayer.putData();
		
		return buffer.array();
	}
	
	
	class RootLayer {
		
		int length = LENGTH - 16; // 22 + frameLayer length + dmpLayer length (22 + 600 for full payload)
		
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
		
		public FramingLayer(int universe) {
			this.universe = universe;
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
			buffer.put((byte) 0);						// Sequence Number (1 byte)
			buffer.put((byte) 0);						// Options Flags (1 byte)
			buffer.putShort((short) universe);			// Universe Number (2 bytes)
		}
		
	}
	
	class DmpLayer {
		// TODO
	}

}
