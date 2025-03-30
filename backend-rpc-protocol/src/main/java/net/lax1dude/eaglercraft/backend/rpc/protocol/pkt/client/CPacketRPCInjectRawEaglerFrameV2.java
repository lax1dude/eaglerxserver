package net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;

public class CPacketRPCInjectRawEaglerFrameV2 implements EaglerBackendRPCPacket {

	public int packetID;
	public byte[] messageData;

	public CPacketRPCInjectRawEaglerFrameV2() {
	}

	public CPacketRPCInjectRawEaglerFrameV2(int packetID, byte[] messageData) {
		this.packetID = packetID;
		this.messageData = messageData;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		packetID = buffer.readUnsignedShort();
		messageData = new byte[buffer.readInt()];
		buffer.readFully(messageData);
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		buffer.writeShort(packetID);
		buffer.writeInt(messageData.length);
		buffer.write(messageData);
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleClient(this);
	}

	@Override
	public int length() {
		return 2 + 4 + messageData.length;
	}

}
