package net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server;

import java.io.IOException;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketInputBuffer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketOutputBuffer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;

public class SPacketOtherTexturesV5EAG implements GameMessagePacket {

	public long uuidMost;
	public long uuidLeast;
	public int skinID;
	public byte[] customSkin;
	public int capeID;
	public byte[] customCape;

	public SPacketOtherTexturesV5EAG() {
	}

	public SPacketOtherTexturesV5EAG(long uuidMost, long uuidLeast, int skinID, byte[] customSkin,
			int capeID, byte[] customCape) {
		this.uuidMost = uuidMost;
		this.uuidLeast = uuidLeast;
		this.skinID = skinID;
		this.customSkin = customSkin;
		this.capeID = capeID;
		this.customCape = customCape;
	}

	@Override
	public void readPacket(GamePacketInputBuffer buffer) throws IOException {
		uuidMost = buffer.readLong();
		uuidLeast = buffer.readLong();
		skinID = buffer.readInt();
		capeID = buffer.readInt();
		if(skinID < 0) {
			customSkin = new byte[12288];
			buffer.readFully(customSkin);
		}
		if(capeID < 0) {
			customCape = new byte[1173];
			buffer.readFully(customCape);
		}
	}

	@Override
	public void writePacket(GamePacketOutputBuffer buffer) throws IOException {
		buffer.writeLong(uuidMost);
		buffer.writeLong(uuidLeast);
		buffer.writeInt(skinID);
		buffer.writeInt(capeID);
		if(skinID < 0) {
			if(customSkin.length != 12288) {
				throw new IOException("Custom skin data length is not 12288 bytes! (" + customSkin.length + ")");
			}
			buffer.write(customSkin);
		}
		if(capeID < 0) {
			if(customCape.length != 1173) {
				throw new IOException("Custom cape data length is not 1173 bytes! (" + customCape.length + ")");
			}
			buffer.write(customCape);
		}
	}

	@Override
	public void handlePacket(GameMessageHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		int i = 25;
		if(skinID < 0) i += 12288;
		if(capeID < 0) i += 1173;
		return i;
	}

}
