package net.lax1dude.eaglercraft.backend.server.base.voice;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import net.lax1dude.eaglercraft.backend.rpc.protocol.EaglerBackendRPCProtocol;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.server.base.DataSerializationContext;
import net.lax1dude.eaglercraft.backend.voice.protocol.EaglerVCProtocol;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCPacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.ReusableByteArrayInputStream;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.ReusableByteArrayOutputStream;

abstract class SerializationContext {

	private final ReusableByteArrayInputStream byteInputStreamSingleton;
	private final ReusableByteArrayOutputStream byteOutputStreamSingleton;
	private final DataInputStream inputStreamSingleton;
	private final DataOutputStream outputStreamSingleton;
	private final AtomicBoolean inputStreamLock;
	private final AtomicBoolean outputStreamLock;

	SerializationContext() {
		this.byteInputStreamSingleton = new ReusableByteArrayInputStream();
		this.byteOutputStreamSingleton = new ReusableByteArrayOutputStream();
		this.inputStreamSingleton = new DataInputStream(byteInputStreamSingleton);
		this.outputStreamSingleton = new DataOutputStream(byteOutputStreamSingleton);
		this.inputStreamLock = new AtomicBoolean(false);
		this.outputStreamLock = new AtomicBoolean(false);
	}

	SerializationContext(DataSerializationContext context) {
		this.byteInputStreamSingleton = context.byteInputStreamSingleton;
		this.byteOutputStreamSingleton = context.byteOutputStreamSingleton;
		this.inputStreamSingleton = context.inputStreamSingleton;
		this.outputStreamSingleton = context.outputStreamSingleton;
		this.inputStreamLock = context.inputStreamLock;
		this.outputStreamLock = context.outputStreamLock;
	}

	protected abstract IPlatformLogger logger();

	EaglerVCPacket deserialize(EaglerVCProtocol protocol, byte[] packet) throws IOException {
		if(packet.length == 0) {
			throw new IOException("Empty packet recieved");
		}
		if(!inputStreamLock.getAndSet(true)) {
			try {
				byteInputStreamSingleton.feedBuffer(packet);
				return protocol.readPacket(inputStreamSingleton, EaglerVCProtocol.SERVER_TO_CLIENT);
			}finally {
				byteInputStreamSingleton.feedBuffer(null);
				inputStreamLock.set(false);
			}
		}else {
			ReusableByteArrayInputStream tmp = new ReusableByteArrayInputStream();
			tmp.feedBuffer(packet);
			return protocol.readPacket(new DataInputStream(tmp), EaglerBackendRPCProtocol.SERVER_TO_CLIENT);
		}
	}

	byte[] serialize(EaglerVCProtocol protocol, EaglerVCPacket packet) throws IOException {
		int len = packet.length() + 1;
		byte[] ret;
		if(!outputStreamLock.getAndSet(true)) {
			try {
				byteOutputStreamSingleton.feedBuffer(new byte[len == 0 ? 64 : len]);
				protocol.writePacket(outputStreamSingleton, EaglerBackendRPCProtocol.CLIENT_TO_SERVER, packet);
				ret = byteOutputStreamSingleton.returnBuffer();
			}finally {
				outputStreamLock.set(false);
			}
		}else {
			ReusableByteArrayOutputStream bao = new ReusableByteArrayOutputStream();
			bao.feedBuffer(new byte[len == 0 ? 64 : len]);
			protocol.writePacket(new DataOutputStream(bao), EaglerBackendRPCProtocol.CLIENT_TO_SERVER, packet);
			ret = bao.returnBuffer();
		}
		if(len != 0 && ret.length != len) {
			logger().warn("Packet " + packet.getClass().getSimpleName() + " was the wrong length after serialization, "
					+ ret.length + " != " + len);
		}
		return ret;
	}

}
