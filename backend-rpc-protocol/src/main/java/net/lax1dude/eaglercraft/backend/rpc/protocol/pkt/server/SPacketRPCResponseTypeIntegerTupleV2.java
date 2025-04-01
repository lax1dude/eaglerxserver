package net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;

public class SPacketRPCResponseTypeIntegerTupleV2 implements EaglerBackendRPCPacket {

	public int requestID;
	public int valueA;
	public int valueB;

	public SPacketRPCResponseTypeIntegerTupleV2() {
	}

	public SPacketRPCResponseTypeIntegerTupleV2(int requestID, int valueA, int valueB) {
		this.requestID = requestID;
		this.valueA = valueA;
		this.valueB = valueB;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		requestID = buffer.readInt();
		valueA = buffer.readInt();
		valueB = buffer.readInt();
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		buffer.writeInt(requestID);
		buffer.writeInt(valueA);
		buffer.writeInt(valueB);
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		return 12;
	}

}
