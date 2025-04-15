package net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;

public class CPacketRPCGetSkinByURLV2 implements EaglerBackendRPCPacket {

	public int requestID;
	public String skinURL;

	public CPacketRPCGetSkinByURLV2() {
	}

	public CPacketRPCGetSkinByURLV2(int requestID, String skinURL) {
		this.requestID = requestID;
		this.skinURL = skinURL;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		requestID = buffer.readInt();
		skinURL = EaglerBackendRPCPacket.readString(buffer, 65535, true, StandardCharsets.US_ASCII);
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		buffer.writeInt(requestID);
		EaglerBackendRPCPacket.writeString(buffer, skinURL, true, StandardCharsets.US_ASCII);
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleClient(this);
	}

	@Override
	public int length() {
		return 6 + skinURL.length();
	}

}
