package net.lax1dude.eaglercraft.backend.server.base.message;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.EventLoop;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.BufferUtils;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketOutputBuffer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageConstants;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.ReusableByteArrayInputStream;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.ReusableByteArrayOutputStream;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.SimpleInputBufferImpl;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.SimpleOutputBufferImpl;

public class LegacyMessageController extends MessageController {

	private final ReusableByteArrayInputStream byteInputStreamSingleton = new ReusableByteArrayInputStream();
	private final ReusableByteArrayOutputStream byteOutputStreamSingleton = new ReusableByteArrayOutputStream();
	private final SimpleInputBufferImpl inputStreamSingleton = new SimpleInputBufferImpl(byteInputStreamSingleton);
	private final SimpleOutputBufferImpl outputStreamSingleton = new SimpleOutputBufferImpl(byteOutputStreamSingleton);
	private final byte[] outputTempBuffer = new byte[512];

	private volatile int inputStreamLock;
	private volatile int outputStreamLock;

	private static final VarHandle IS_LOCK_HANDLE;
	private static final VarHandle OS_LOCK_HANDLE;

	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			IS_LOCK_HANDLE = l.findVarHandle(LegacyMessageController.class, "inputStreamLock", int.class);
			OS_LOCK_HANDLE = l.findVarHandle(LegacyMessageController.class, "outputStreamLock", int.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public LegacyMessageController(GamePluginMessageProtocol protocol, ServerMessageHandler handler, EventLoop eventLoop,
			int defragSendDelay) {
		super(protocol, handler, eventLoop, defragSendDelay);
	}

	public boolean readPacket(String channel, byte[] data) {
		if(data.length == 0) {
			return false;
		}
		try {
			GameMessagePacket pkt;
			if((int) IS_LOCK_HANDLE.compareAndExchangeAcquire(this, 0, 1) == 0) {
				try {
					byteInputStreamSingleton.feedBuffer(data);
					if(data[0] == (byte)0xFF && channel.equals(GamePluginMessageConstants.V4_CHANNEL)) {
						inputStreamSingleton.readByte();
						int count = inputStreamSingleton.readVarInt();
						for(int i = 0, j, k; i < count; ++i) {
							j = inputStreamSingleton.readVarInt();
							inputStreamSingleton.setToByteArrayReturns(j - 1);
							k = byteInputStreamSingleton.getReaderIndex() + j;
							if(j < 0 || j > inputStreamSingleton.available()) {
								throw new IOException("Packet fragment is too long: " + j + " > " + inputStreamSingleton.available());
							}
							pkt = protocol.readPacket(channel, GamePluginMessageConstants.CLIENT_TO_SERVER, inputStreamSingleton);
							if(pkt != null) {
								handlePacket(pkt);
							}else {
								throw new IOException("Unknown packet type in fragment!");
							}
							if(byteInputStreamSingleton.getReaderIndex() != k) {
								throw new IOException("Packet fragment was the wrong length: " + (j + byteInputStreamSingleton.getReaderIndex() - k) + " != " + j);
							}
						}
						if(inputStreamSingleton.available() > 0) {
							throw new IOException("Leftover data after reading multi-packet! (" + inputStreamSingleton.available() + " bytes)");
						}
						return true;
					}
					inputStreamSingleton.setToByteArrayReturns(data);
					pkt = protocol.readPacket(channel, GamePluginMessageConstants.CLIENT_TO_SERVER, inputStreamSingleton);
					if(pkt != null && byteInputStreamSingleton.available() != 0) {
						throw new IOException("Packet was the wrong length: " + pkt.getClass().getSimpleName());
					}
				}finally {
					byteInputStreamSingleton.feedBuffer(null);
					inputStreamSingleton.setToByteArrayReturns(null);
					IS_LOCK_HANDLE.setRelease(this, 0);
				}
			}else {
				// slow version that makes multiple new objects
				ReusableByteArrayInputStream inputStream = new ReusableByteArrayInputStream();
				inputStream.feedBuffer(data);
				SimpleInputBufferImpl inputBuffer = new SimpleInputBufferImpl(inputStream, data);
				if(data[0] == (byte)0xFF && channel.equals(GamePluginMessageConstants.V4_CHANNEL)) {
					inputBuffer.readByte();
					int count = inputBuffer.readVarInt();
					for(int i = 0, j, k; i < count; ++i) {
						j = inputBuffer.readVarInt();
						inputBuffer.setToByteArrayReturns(j - 1);
						k = inputStream.getReaderIndex() + j;
						if(j < 0 || j > inputBuffer.available()) {
							throw new IOException("Packet fragment is too long: " + j + " > " + inputBuffer.available());
						}
						pkt = protocol.readPacket(channel, GamePluginMessageConstants.CLIENT_TO_SERVER, inputBuffer);
						if(pkt != null) {
							handlePacket(pkt);
						}else {
							throw new IOException("Unknown packet type in fragment!");
						}
						if(inputStream.getReaderIndex() != k) {
							throw new IOException("Packet fragment was the wrong length: " + (j + inputStream.getReaderIndex() - k) + " != " + j);
						}
					}
					if(inputBuffer.available() > 0) {
						throw new IOException("Leftover data after reading multi-packet! (" + inputBuffer.available() + " bytes)");
					}
					return true;
				}
				pkt = protocol.readPacket(channel, GamePluginMessageConstants.CLIENT_TO_SERVER, inputBuffer);
				if(pkt != null && inputStream.available() != 0) {
					throw new IOException("Packet was the wrong length: " + pkt.getClass().getSimpleName());
				}
			}
			if(pkt != null) {
				handlePacket(pkt);
				return true;
			}else {
				return false;
			}
		}catch(IOException ex) {
			onException(ex);
			return true;
		}
	}

	@Override
	protected void writePacket(GameMessagePacket packet) throws IOException {
		int len = packet.length() + 1;
		String chan;
		byte[] data;
		if((int) OS_LOCK_HANDLE.compareAndExchangeAcquire(this, 0, 1) == 0) {
			try {
				byteOutputStreamSingleton.feedBuffer(len == 0 ? outputTempBuffer : new byte[len]);
				chan = protocol.writePacket(GamePluginMessageConstants.SERVER_TO_CLIENT, outputStreamSingleton, packet);
				data = len == 0 ? byteOutputStreamSingleton.returnBufferCopied()
						: byteOutputStreamSingleton.returnBuffer();
			}finally {
				byteOutputStreamSingleton.feedBuffer(null);
				OS_LOCK_HANDLE.setRelease(this, 0);
			}
		}else {
			// slow version that makes multiple new objects
			ReusableByteArrayOutputStream bao = new ReusableByteArrayOutputStream();
			bao.feedBuffer(new byte[len == 0 ? 64 : len]);
			SimpleOutputBufferImpl outputStream = new SimpleOutputBufferImpl(bao);
			chan = protocol.writePacket(GamePluginMessageConstants.SERVER_TO_CLIENT, outputStream, packet);
			data = bao.returnBuffer();
		}
		EaglerPlayerInstance<?> player = ((ServerMessageHandler)handler).eaglerHandle;
		if(len != 0 && data.length != len && (protocol.ver > 3 || data.length + 1 != len)) {
			player.getEaglerXServer().logger().warn("Packet " + packet.getClass().getSimpleName()
					+ " was the wrong length after serialization, " + data.length + " != " + len);
		}
		player.getPlatformPlayer().sendDataClient(chan, data);
	}

	@Override
	protected void writeMultiPacket(GameMessagePacket[] packets) throws IOException {
		int total = packets.length;
		EaglerPlayerInstance<?> player = ((ServerMessageHandler)handler).eaglerHandle;
		byte[][] buffer = new byte[total][];
		byte[] dat;
		if((int) OS_LOCK_HANDLE.compareAndExchangeAcquire(this, 0, 1) == 0) {
			try {
				for(int i = 0; i < total; ++i) {
					GameMessagePacket packet = packets[i];
					int len = packet.length() + 1;
					byteOutputStreamSingleton.feedBuffer(len == 0 ? outputTempBuffer : new byte[len]);
					protocol.writePacket(GamePluginMessageConstants.SERVER_TO_CLIENT, outputStreamSingleton, packet);
					dat = len == 0 ? byteOutputStreamSingleton.returnBufferCopied()
							: byteOutputStreamSingleton.returnBuffer();
					if(len != 0 && dat.length != len) {
						player.getEaglerXServer().logger().warn("Packet " + packet.getClass().getSimpleName()
								+ " was the wrong length after serialization, " + dat.length + " != " + len);
					}
					buffer[i] = dat;
				}
			}finally {
				byteOutputStreamSingleton.feedBuffer(null);
				OS_LOCK_HANDLE.setRelease(this, 0);
			}
		}else {
			ReusableByteArrayOutputStream bao = new ReusableByteArrayOutputStream();
			SimpleOutputBufferImpl outputStream = new SimpleOutputBufferImpl(bao);
			for(int i = 0; i < total; ++i) {
				GameMessagePacket packet = packets[i];
				int len = packet.length() + 1;
				bao.feedBuffer(new byte[len == 0 ? 64 : len]);
				protocol.writePacket(GamePluginMessageConstants.SERVER_TO_CLIENT, outputStream, packet);
				dat = bao.returnBuffer();
				if(len != 0 && dat.length != len) {
					player.getEaglerXServer().logger().warn("Packet " + packet.getClass().getSimpleName()
							+ " was the wrong length after serialization, " + dat.length + " != " + len);
				}
				buffer[i] = dat;
			}
		}
		int start = 0;
		int i, j, sendCount, totalLen, lastLen;
		while(total > start) {
			sendCount = 0;
			totalLen = 0;
			do {
				i = buffer[start + sendCount].length;
				lastLen = GamePacketOutputBuffer.getVarIntSize(i) + i;
				totalLen += lastLen;
				++sendCount;
			}while(totalLen < 32760 && sendCount < total - start);
			if(totalLen >= 32760) {
				--sendCount;
				totalLen -= lastLen;
			}
			if(sendCount <= 1) {
				player.getPlatformPlayer().sendDataClient(GamePluginMessageConstants.V4_CHANNEL, buffer[start++]);
				continue;
			}
			byte[] toSend = new byte[1 + totalLen + GamePacketOutputBuffer.getVarIntSize(sendCount)];
			ByteBuf sendBuffer = Unpooled.wrappedBuffer(toSend);
			sendBuffer.writerIndex(0);
			sendBuffer.writeByte(0xFF);
			BufferUtils.writeVarInt(sendBuffer, sendCount);
			for(j = 0; j < sendCount; ++j) {
				dat = buffer[start++];
				BufferUtils.writeVarInt(sendBuffer, dat.length);
				sendBuffer.writeBytes(dat);
			}
			player.getPlatformPlayer().sendDataClient(GamePluginMessageConstants.V4_CHANNEL, toSend);
		}
	}

}
