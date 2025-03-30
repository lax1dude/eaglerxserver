package net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;

public class SPacketRPCResponseTypeTexturesV2 implements EaglerBackendRPCPacket {

	public int requestID;
	public byte[] skinResponse;
	public byte[] capeResponse;

	public SPacketRPCResponseTypeTexturesV2() {
	}

	public SPacketRPCResponseTypeTexturesV2(int requestID, byte[] skinResponse, byte[] capeResponse) {
		this.requestID = requestID;
		this.skinResponse = skinResponse;
		this.capeResponse = capeResponse;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		requestID = buffer.readInt();
		int l = buffer.readUnsignedShort();
		if(l > 0) {
			skinResponse = new byte[l];
			buffer.readFully(skinResponse);
		}else {
			skinResponse = null;
		}
		l = buffer.readUnsignedShort();
		if(l > 0) {
			capeResponse = new byte[l];
			buffer.readFully(capeResponse);
		}else {
			capeResponse = null;
		}
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		buffer.writeInt(requestID);
		if(skinResponse != null && skinResponse.length > 0) {
			buffer.writeShort(skinResponse.length);
			buffer.write(skinResponse);
		}else {
			buffer.writeShort(0);
		}
		if(capeResponse != null && capeResponse.length > 0) {
			buffer.writeShort(capeResponse.length);
			buffer.write(capeResponse);
		}else {
			buffer.writeShort(0);
		}
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		int l = 8;
		if(skinResponse != null) l += skinResponse.length;
		if(capeResponse != null) l += capeResponse.length;
		return l;
	}

}
