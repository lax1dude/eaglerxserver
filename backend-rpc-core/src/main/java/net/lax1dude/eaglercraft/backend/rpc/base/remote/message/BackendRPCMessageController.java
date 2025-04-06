package net.lax1dude.eaglercraft.backend.rpc.base.remote.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.util.DataSerializationContext;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.util.ReusableByteArrayInputStream;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.util.ReusableByteArrayOutputStream;
import net.lax1dude.eaglercraft.backend.rpc.protocol.EaglerBackendRPCProtocol;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;

public abstract class BackendRPCMessageController {

	private final EaglerBackendRPCProtocol protocol;
	private final DataSerializationContext serializeCtx;

	public BackendRPCMessageController(EaglerBackendRPCProtocol protocol, DataSerializationContext serializeCtx) {
		this.protocol = protocol;
		this.serializeCtx = serializeCtx;
	}

	protected abstract IPlatformLogger logger();

	public EaglerBackendRPCProtocol getProtocol() {
		return protocol;
	}

	protected EaglerBackendRPCPacket deserialize(byte[] packet) throws IOException {
		if(packet.length == 0) {
			throw new IOException("Empty packet recieved");
		}
		if(serializeCtx.aquireInputStream()) {
			try {
				serializeCtx.byteInputStreamSingleton.feedBuffer(packet);
				return protocol.readPacket(serializeCtx.inputStreamSingleton, EaglerBackendRPCProtocol.SERVER_TO_CLIENT);
			}finally {
				serializeCtx.byteInputStreamSingleton.feedBuffer(null);
				serializeCtx.releaseInputStream();
			}
		}else {
			ReusableByteArrayInputStream tmp = new ReusableByteArrayInputStream();
			tmp.feedBuffer(packet);
			return protocol.readPacket(new DataInputStream(tmp), EaglerBackendRPCProtocol.SERVER_TO_CLIENT);
		}
	}

	protected byte[] serialize(EaglerBackendRPCPacket packet) throws IOException {
		int len = packet.length() + 1;
		byte[] ret;
		if(serializeCtx.aquireOutputStream()) {
			try {
				serializeCtx.byteOutputStreamSingleton.feedBuffer(len == 0 ? serializeCtx.outputTempBuffer : new byte[len]);
				protocol.writePacket(serializeCtx.outputStreamSingleton, EaglerBackendRPCProtocol.CLIENT_TO_SERVER, packet);
				ret = len == 0 ? serializeCtx.byteOutputStreamSingleton.returnBufferCopied()
						: serializeCtx.byteOutputStreamSingleton.returnBuffer();
			}finally {
				serializeCtx.releaseOutputStream();
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
