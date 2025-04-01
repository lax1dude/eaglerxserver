package net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;

public class CPacketRPCSetPlayerTexturesV2 implements EaglerBackendRPCPacket {

	public boolean notifyOthers;
	public byte[] texturesPacket;

	public CPacketRPCSetPlayerTexturesV2() {
	}

	public CPacketRPCSetPlayerTexturesV2(boolean notifyOthers, byte[] texturesPacket) {
		this.notifyOthers = notifyOthers;
		this.texturesPacket = texturesPacket;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		notifyOthers = buffer.readBoolean();
		texturesPacket = new byte[buffer.readUnsignedShort()];
		buffer.readFully(texturesPacket);
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		if(texturesPacket.length > 32720) {
			throw new IOException("Texture data cannot be longer than 32720 bytes!");
		}
		buffer.writeBoolean(notifyOthers);
		buffer.writeShort(texturesPacket.length);
		buffer.write(texturesPacket);
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleClient(this);
	}

	@Override
	public int length() {
		return 3 + texturesPacket.length;
	}

}
