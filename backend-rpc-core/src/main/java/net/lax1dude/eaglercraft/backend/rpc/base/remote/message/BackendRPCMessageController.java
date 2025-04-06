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

	protected abstract BackendRPCProtocolHandler handler();

	protected abstract void onException(Exception ex);

	protected abstract void writeOutboundMessage(byte[] data);

	public EaglerBackendRPCProtocol getProtocol() {
		return protocol;
	}

	protected void handleInboundMessage(byte[] packet) {
		EaglerBackendRPCPacket pkt;
		try {
			if(packet.length == 0) {
				throw new IOException("Empty packet recieved");
			}
			if(serializeCtx.aquireInputStream()) {
				try {
					serializeCtx.byteInputStreamSingleton.feedBuffer(packet);
					pkt = protocol.readPacket(serializeCtx.inputStreamSingleton, EaglerBackendRPCProtocol.SERVER_TO_CLIENT);
				}finally {
					serializeCtx.byteInputStreamSingleton.feedBuffer(null);
					serializeCtx.releaseInputStream();
				}
			}else {
				ReusableByteArrayInputStream tmp = new ReusableByteArrayInputStream();
				tmp.feedBuffer(packet);
				pkt = protocol.readPacket(new DataInputStream(tmp), EaglerBackendRPCProtocol.SERVER_TO_CLIENT);
			}
		}catch(Exception ex) {
			onException(ex);
			return;
		}
		try {
			pkt.handlePacket(handler());
		}catch(Exception ex) {
			onException(new IllegalStateException("Failed to process RPC packet: " + pkt.getClass().getSimpleName(), ex));
		}
	}

	protected void writeOutboundPacket(EaglerBackendRPCPacket packet) {
		int len = packet.length() + 1;
		byte[] ret;
		try {
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
		}catch(Exception ex) {
			onException(ex);
			return;
		}
		if(len != 0 && ret.length != len) {
			logger().warn("Packet " + packet.getClass().getSimpleName() + " was the wrong length after serialization, "
					+ ret.length + " != " + len);
		}
		writeOutboundMessage(ret);
	}

	public static EaglerBackendRPCPacket deserializeINIT(byte[] packet, DataSerializationContext serializeCtx) throws IOException {
		if(packet.length == 0) {
			throw new IOException("Empty packet recieved");
		}
		if(serializeCtx.aquireInputStream()) {
			try {
				serializeCtx.byteInputStreamSingleton.feedBuffer(packet);
				return EaglerBackendRPCProtocol.INIT.readPacket(serializeCtx.inputStreamSingleton, EaglerBackendRPCProtocol.SERVER_TO_CLIENT);
			}finally {
				serializeCtx.byteInputStreamSingleton.feedBuffer(null);
				serializeCtx.releaseInputStream();
			}
		}else {
			ReusableByteArrayInputStream tmp = new ReusableByteArrayInputStream();
			tmp.feedBuffer(packet);
			return EaglerBackendRPCProtocol.INIT.readPacket(new DataInputStream(tmp), EaglerBackendRPCProtocol.SERVER_TO_CLIENT);
		}
	}

	public static byte[] serializeINIT(EaglerBackendRPCPacket packet, DataSerializationContext serializeCtx) throws IOException {
		int len = packet.length() + 1;
		byte[] ret;
		if(serializeCtx.aquireOutputStream()) {
			try {
				serializeCtx.byteOutputStreamSingleton.feedBuffer(len == 0 ? serializeCtx.outputTempBuffer : new byte[len]);
				EaglerBackendRPCProtocol.INIT.writePacket(serializeCtx.outputStreamSingleton, EaglerBackendRPCProtocol.CLIENT_TO_SERVER, packet);
				ret = len == 0 ? serializeCtx.byteOutputStreamSingleton.returnBufferCopied()
						: serializeCtx.byteOutputStreamSingleton.returnBuffer();
			}finally {
				serializeCtx.releaseOutputStream();
			}
		}else {
			ReusableByteArrayOutputStream bao = new ReusableByteArrayOutputStream();
			bao.feedBuffer(new byte[len == 0 ? 64 : len]);
			EaglerBackendRPCProtocol.INIT.writePacket(new DataOutputStream(bao), EaglerBackendRPCProtocol.CLIENT_TO_SERVER, packet);
			ret = bao.returnBuffer();
		}
		return ret;
	}

}
