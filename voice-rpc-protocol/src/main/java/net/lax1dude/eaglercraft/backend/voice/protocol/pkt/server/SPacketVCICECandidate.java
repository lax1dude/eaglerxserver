package net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCHandler;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCPacket;

public class SPacketVCICECandidate implements EaglerVCPacket {

	public long uuidMost;
	public long uuidLeast;
	public byte[] desc;

	public SPacketVCICECandidate() {
	}

	public SPacketVCICECandidate(long uuidMost, long uuidLeast, byte[] desc) {
		this.uuidMost = uuidMost;
		this.uuidLeast = uuidLeast;
		this.desc = desc;
	}

	public SPacketVCICECandidate(long uuidMost, long uuidLeast, String desc) {
		this.uuidMost = uuidMost;
		this.uuidLeast = uuidLeast;
		this.desc = desc.getBytes(StandardCharsets.UTF_8);
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		uuidMost = buffer.readLong();
		uuidLeast = buffer.readLong();
		int descLen = buffer.readUnsignedShort();
		if(descLen > 32750) {
			throw new IOException("Voice signal packet ICE too long!");
		}
		desc = new byte[descLen];
		buffer.readFully(desc);
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		if(desc.length > 32750) {
			throw new IOException("Voice signal packet ICE too long!");
		}
		buffer.writeLong(uuidMost);
		buffer.writeLong(uuidLeast);
		buffer.writeShort(desc.length);
		buffer.write(desc);
	}

	@Override
	public void handlePacket(EaglerVCHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		return 18 + desc.length;
	}

}
