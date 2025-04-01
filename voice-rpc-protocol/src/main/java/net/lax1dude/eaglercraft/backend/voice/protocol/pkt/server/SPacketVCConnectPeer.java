package net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCHandler;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCPacket;

public class SPacketVCConnectPeer implements EaglerVCPacket {

	public long uuidMost;
	public long uuidLeast;
	public boolean offer;

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		uuidMost = buffer.readLong();
		uuidLeast = buffer.readLong();
		offer = buffer.readBoolean();
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		buffer.writeLong(uuidMost);
		buffer.writeLong(uuidLeast);
		buffer.writeBoolean(offer);
	}

	@Override
	public void handlePacket(EaglerVCHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		return 17;
	}

}
