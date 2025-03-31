package net.lax1dude.eaglercraft.backend.server.base.rpc;

import java.io.IOException;

import net.lax1dude.eaglercraft.backend.rpc.protocol.EaglerBackendRPCProtocol;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;

public class SerializationContext {

	private final EaglerBackendRPCProtocol protocol;

	SerializationContext(EaglerBackendRPCProtocol protocol) {
		this.protocol = protocol;
	}

	EaglerBackendRPCProtocol getProtocol() {
		return protocol;
	}

	byte[] serialize(EaglerBackendRPCPacket packet) throws IOException {
		return null; //TODO
	}

	EaglerBackendRPCPacket deserialize(byte[] packet) throws IOException {
		return null; //TODO
	}

}
