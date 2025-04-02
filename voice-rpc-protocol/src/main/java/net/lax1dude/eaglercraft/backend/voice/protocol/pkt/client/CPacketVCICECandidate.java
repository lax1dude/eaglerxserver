package net.lax1dude.eaglercraft.backend.voice.protocol.pkt.client;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCHandler;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCPacket;

public class CPacketVCICECandidate implements EaglerVCPacket {

	public long uuidMost;
	public long uuidLeast;
	public byte[] ice;

	public CPacketVCICECandidate() {
	}

	public CPacketVCICECandidate(long uuidMost, long uuidLeast, byte[] ice) {
		this.uuidMost = uuidMost;
		this.uuidLeast = uuidLeast;
		this.ice = ice;
	}

	public CPacketVCICECandidate(long uuidMost, long uuidLeast, String ice) {
		this.uuidMost = uuidMost;
		this.uuidLeast = uuidLeast;
		this.ice = ice.getBytes(StandardCharsets.UTF_8);
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		uuidMost = buffer.readLong();
		uuidLeast = buffer.readLong();
		int descLen = buffer.readUnsignedShort();
		if(descLen > 32750) {
			throw new IOException("Voice signal packet ICE too long!");
		}
		ice = new byte[descLen];
		buffer.readFully(ice);
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		if(ice.length > 32750) {
			throw new IOException("Voice signal packet ICE too long!");
		}
		buffer.writeLong(uuidMost);
		buffer.writeLong(uuidLeast);
		buffer.writeShort(ice.length);
		buffer.write(ice);
	}

	@Override
	public void handlePacket(EaglerVCHandler handler) {
		handler.handleClient(this);
	}

	@Override
	public int length() {
		return 18 + ice.length;
	}

}
