package net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;

public class CPacketRPCGetCapeByURLV2 implements EaglerBackendRPCPacket {

	public int requestID;
	public String capeURL;

	public CPacketRPCGetCapeByURLV2() {
	}

	public CPacketRPCGetCapeByURLV2(int requestID, String capeURL) {
		this.requestID = requestID;
		this.capeURL = capeURL;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		requestID = buffer.readInt();
		capeURL = EaglerBackendRPCPacket.readString(buffer, 65535, true, StandardCharsets.US_ASCII);
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		buffer.writeInt(requestID);
		EaglerBackendRPCPacket.writeString(buffer, capeURL, true, StandardCharsets.US_ASCII);
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleClient(this);
	}

	@Override
	public int length() {
		return 6 + capeURL.length();
	}

}
