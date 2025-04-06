package net.lax1dude.eaglercraft.backend.server.base.voice;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.rpc.protocol.EaglerBackendRPCProtocol;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.server.base.DataSerializationContext;
import net.lax1dude.eaglercraft.backend.voice.protocol.EaglerVCProtocol;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCPacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.ReusableByteArrayInputStream;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.ReusableByteArrayOutputStream;

abstract class SerializationContext {

	private final DataSerializationContext ctx;

	SerializationContext() {
		this.ctx = new DataSerializationContext();
	}

	SerializationContext(DataSerializationContext context) {
		this.ctx = context;
	}

	protected abstract IPlatformLogger logger();

	EaglerVCPacket deserialize(EaglerVCProtocol protocol, byte[] packet) throws IOException {
		if(packet.length == 0) {
			throw new IOException("Empty packet recieved");
		}
		if(ctx.aquireInputStream()) {
			try {
				ctx.byteInputStreamSingleton.feedBuffer(packet);
				return protocol.readPacket(ctx.inputStreamSingleton, EaglerVCProtocol.SERVER_TO_CLIENT);
			}finally {
				ctx.byteInputStreamSingleton.feedBuffer(null);
				ctx.releaseInputStream();
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
		if(ctx.aquireOutputStream()) {
			try {
				ctx.byteOutputStreamSingleton.feedBuffer(len == 0 ? ctx.outputTempBuffer : new byte[len]);
				protocol.writePacket(ctx.outputStreamSingleton, EaglerBackendRPCProtocol.CLIENT_TO_SERVER, packet);
				ret = len == 0 ? ctx.byteOutputStreamSingleton.returnBufferCopied()
						: ctx.byteOutputStreamSingleton.returnBuffer();
			}finally {
				ctx.releaseOutputStream();
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
