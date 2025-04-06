package net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;
import net.lax1dude.eaglercraft.backend.rpc.protocol.util.IInteger;

public class SPacketRPCResponseTypeIntegerSingleV2 implements EaglerBackendRPCPacket, IInteger {

	public int requestID;
	public int value;

	public SPacketRPCResponseTypeIntegerSingleV2() {
	}

	public SPacketRPCResponseTypeIntegerSingleV2(int requestID, int value) {
		this.requestID = requestID;
		this.value = value;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		requestID = buffer.readInt();
		value = buffer.readInt();
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		buffer.writeInt(requestID);
		buffer.writeInt(value);
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		return 8;
	}

	@Override
	public int getIntValue() {
		return value;
	}

}
