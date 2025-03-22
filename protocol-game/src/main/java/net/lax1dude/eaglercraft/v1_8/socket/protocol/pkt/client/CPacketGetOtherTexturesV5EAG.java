package net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.client;

import java.io.IOException;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketInputBuffer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketOutputBuffer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;

public class CPacketGetOtherTexturesV5EAG implements GameMessagePacket {

	public long uuidMost;
	public long uuidLeast;

	public CPacketGetOtherTexturesV5EAG() {
	}

	public CPacketGetOtherTexturesV5EAG(long uuidMost, long uuidLeast) {
		this.uuidMost = uuidMost;
		this.uuidLeast = uuidLeast;
	}

	@Override
	public void readPacket(GamePacketInputBuffer buffer) throws IOException {
		uuidMost = buffer.readLong();
		uuidLeast = buffer.readLong();
	}

	@Override
	public void writePacket(GamePacketOutputBuffer buffer) throws IOException {
		buffer.writeLong(uuidMost);
		buffer.writeLong(uuidLeast);
	}

	@Override
	public void handlePacket(GameMessageHandler handler) {
		handler.handleClient(this);
	}

	@Override
	public int length() {
		return 16;
	}

}
