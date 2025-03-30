package net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;

public class CPacketRPCInjectRawBinaryFrameV2 implements EaglerBackendRPCPacket {

	public byte[] messageData;

	public CPacketRPCInjectRawBinaryFrameV2() {
	}

	public CPacketRPCInjectRawBinaryFrameV2(byte[] messageData) {
		this.messageData = messageData;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		messageData = new byte[buffer.readInt()];
		buffer.readFully(messageData);
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		buffer.writeInt(messageData.length);
		buffer.write(messageData);
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleClient(this);
	}

	@Override
	public int length() {
		return 4 + messageData.length;
	}

}
