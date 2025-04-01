package net.lax1dude.eaglercraft.backend.voice.protocol.pkt.client;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCHandler;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCPacket;

public class CPacketVCDisconnect implements EaglerVCPacket {

	public CPacketVCDisconnect() {
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
	}

	@Override
	public void handlePacket(EaglerVCHandler handler) {
		handler.handleClient(this);
	}

	@Override
	public int length() {
		return 0;
	}

}
