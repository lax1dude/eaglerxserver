package net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCHandler;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCPacket;

public class SPacketVCAllowed implements EaglerVCPacket {

	public boolean allowed;

	public SPacketVCAllowed() {
	}

	public SPacketVCAllowed(boolean allowed) {
		this.allowed = allowed;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		allowed = buffer.readBoolean();
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		buffer.writeBoolean(allowed);
	}

	@Override
	public void handlePacket(EaglerVCHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		return 1;
	}

}
