package net.lax1dude.eaglercraft.backend.server.base.message;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

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
	private final AtomicBoolean inputStreamLock = new AtomicBoolean(false);
	private final AtomicBoolean outputStreamLock = new AtomicBoolean(false);

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
			if(!inputStreamLock.getAndSet(true)) {
				try {
					byteInputStreamSingleton.feedBuffer(data);
					if(data[0] == (byte)0xFF && channel.equals(GamePluginMessageConstants.V4_CHANNEL)) {
						inputStreamSingleton.readByte();
						int count = inputStreamSingleton.readVarInt();
						for(int i = 0, j, k; i < count; ++i) {
							j = inputStreamSingleton.readVarInt();
							k = byteInputStreamSingleton.getReaderIndex() + j;
							if(j > inputStreamSingleton.available()) {
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
					inputStreamLock.set(false);
				}
			}else {
				// slow version that makes multiple new objects
				ReusableByteArrayInputStream inputStream = new ReusableByteArrayInputStream();
				inputStream.feedBuffer(data);
				SimpleInputBufferImpl inputBuffer = new SimpleInputBufferImpl(inputStream, data);
				if(data[0] == (byte)0xFF && channel.equals(GamePluginMessageConstants.V4_CHANNEL)) {
					inputBuffer.setToByteArrayReturns(null);
					inputBuffer.readByte();
					int count = inputBuffer.readVarInt();
					for(int i = 0, j, k; i < count; ++i) {
						j = inputBuffer.readVarInt();
						k = inputStream.getReaderIndex() + j;
						if(j > inputBuffer.available()) {
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
		if(!outputStreamLock.getAndSet(true)) {
			try {
				byteOutputStreamSingleton.feedBuffer(new byte[len == 0 ? 64 : len]);
				chan = protocol.writePacket(GamePluginMessageConstants.SERVER_TO_CLIENT, outputStreamSingleton, packet);
				data = byteOutputStreamSingleton.returnBuffer();
			}finally {
				byteOutputStreamSingleton.feedBuffer(null);
				outputStreamLock.set(false);
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
		if(len != 0 && data.length != len && data.length + 1 != len) {
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
		if(!outputStreamLock.getAndSet(true)) {
			try {
				for(int i = 0; i < packets.length; ++i) {
					GameMessagePacket packet = packets[i];
					int len = packet.length() + 1;
					byteOutputStreamSingleton.feedBuffer(new byte[len == 0 ? 64 : len]);
					protocol.writePacket(GamePluginMessageConstants.SERVER_TO_CLIENT, outputStreamSingleton, packet);
					dat = byteOutputStreamSingleton.returnBuffer();
					if(len != 0 && dat.length != len && dat.length + 1 != len) {
						player.getEaglerXServer().logger().warn("Packet " + packet.getClass().getSimpleName()
								+ " was the wrong length after serialization, " + dat.length + " != " + len);
					}
					buffer[i] = dat;
				}
			}finally {
				byteOutputStreamSingleton.feedBuffer(null);
				outputStreamLock.set(false);
			}
		}else {
			ReusableByteArrayOutputStream bao = new ReusableByteArrayOutputStream();
			SimpleOutputBufferImpl outputStream = new SimpleOutputBufferImpl(bao);
			for(int i = 0; i < packets.length; ++i) {
				GameMessagePacket packet = packets[i];
				int len = packet.length() + 1;
				bao.feedBuffer(new byte[len == 0 ? 64 : len]);
				protocol.writePacket(GamePluginMessageConstants.SERVER_TO_CLIENT, outputStream, packet);
				dat = bao.returnBuffer();
				if(len != 0 && dat.length != len && dat.length + 1 != len) {
					player.getEaglerXServer().logger().warn("Packet " + packet.getClass().getSimpleName()
							+ " was the wrong length after serialization, " + dat.length + " != " + len);
				}
				buffer[i] = dat;
			}
		}
		int start = 0;
		int i, j, sendCount, totalLen, lastLen;
		while(total - start > 0) {
			sendCount = 0;
			totalLen = 0;
			do {
				i = buffer[start + sendCount].length;
				lastLen = GamePacketOutputBuffer.getVarIntSize(i) + i;
				totalLen += lastLen;
				++sendCount;
			}while(totalLen < 32760 && total - start - sendCount > 0);
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
