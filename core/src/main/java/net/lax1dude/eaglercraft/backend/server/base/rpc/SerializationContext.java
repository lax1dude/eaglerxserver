package net.lax1dude.eaglercraft.backend.server.base.rpc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import net.lax1dude.eaglercraft.backend.rpc.protocol.EaglerBackendRPCProtocol;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.ReusableByteArrayInputStream;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.ReusableByteArrayOutputStream;

abstract class SerializationContext {

	private final EaglerBackendRPCProtocol protocol;
	private final ReusableByteArrayInputStream byteInputStreamSingleton = new ReusableByteArrayInputStream();
	private final ReusableByteArrayOutputStream byteOutputStreamSingleton = new ReusableByteArrayOutputStream();
	private final DataInputStream inputStreamSingleton = new DataInputStream(byteInputStreamSingleton);
	private final DataOutputStream outputStreamSingleton = new DataOutputStream(byteOutputStreamSingleton);
	private final AtomicBoolean inputStreamLock = new AtomicBoolean(false);
	private final AtomicBoolean outputStreamLock = new AtomicBoolean(false);

	SerializationContext(EaglerBackendRPCProtocol protocol) {
		this.protocol = protocol;
	}

	protected abstract IPlatformLogger logger();

	EaglerBackendRPCProtocol getProtocol() {
		return protocol;
	}

	EaglerBackendRPCPacket deserialize(byte[] packet) throws IOException {
		if(packet.length == 0) {
			throw new IOException("Empty packet recieved");
		}
		if(!inputStreamLock.getAndSet(true)) {
			try {
				byteInputStreamSingleton.feedBuffer(packet);
				return protocol.readPacket(inputStreamSingleton, EaglerBackendRPCProtocol.CLIENT_TO_SERVER);
			}finally {
				byteInputStreamSingleton.feedBuffer(null);
				inputStreamLock.set(false);
			}
		}else {
			ReusableByteArrayInputStream tmp = new ReusableByteArrayInputStream();
			tmp.feedBuffer(packet);
			return protocol.readPacket(new DataInputStream(tmp), EaglerBackendRPCProtocol.CLIENT_TO_SERVER);
		}
	}

	byte[] serialize(EaglerBackendRPCPacket packet) throws IOException {
		int len = packet.length() + 1;
		byte[] ret;
		if(!outputStreamLock.getAndSet(true)) {
			try {
				byteOutputStreamSingleton.feedBuffer(new byte[len == 0 ? 64 : len]);
				protocol.writePacket(outputStreamSingleton, EaglerBackendRPCProtocol.SERVER_TO_CLIENT, packet);
				ret = byteOutputStreamSingleton.returnBuffer();
			}finally {
				outputStreamLock.set(false);
			}
		}else {
			ReusableByteArrayOutputStream bao = new ReusableByteArrayOutputStream();
			bao.feedBuffer(new byte[len == 0 ? 64 : len]);
			protocol.writePacket(new DataOutputStream(bao), EaglerBackendRPCProtocol.SERVER_TO_CLIENT, packet);
			ret = bao.returnBuffer();
		}
		if(len != 0 && ret.length != len) {
			logger().warn("Packet " + packet.getClass().getSimpleName() + " was the wrong length after serialization, "
					+ ret.length + " != " + len);
		}
		return ret;
	}

}
