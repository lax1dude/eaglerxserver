package net.lax1dude.eaglercraft.backend.server.base;

import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import net.lax1dude.eaglercraft.backend.server.base.handshake.HandshakePacketTypes;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.BufferUtils;

class RateLimitMessage {

	private static final ByteBuf blockedLogin = generateLoginKick(HandshakePacketTypes.SERVER_ERROR_RATELIMIT_BLOCKED, "Too many logins!");
	private static final ByteBuf lockedLogin = generateLoginKick(HandshakePacketTypes.SERVER_ERROR_RATELIMIT_LOCKED, "Too many logins!");

	private static ByteBuf generateLoginKick(int code, String msg) {
		ByteBuf buf = Unpooled.buffer();
		try {
			buf.writeByte(HandshakePacketTypes.PROTOCOL_SERVER_ERROR);
			buf.writeByte(code);
			buf.writeByte(msg.length());
			BufferUtils.writeCharSequence(buf, msg, StandardCharsets.US_ASCII);
			return buf.retain();
		}finally {
			buf.release();
		}
	}

	static BinaryWebSocketFrame getBlockedLoginMessage() {
		return new BinaryWebSocketFrame(Unpooled.wrappedBuffer(blockedLogin));
	}

	static BinaryWebSocketFrame getLockedLoginMessage() {
		return new BinaryWebSocketFrame(Unpooled.wrappedBuffer(lockedLogin));
	}

	static TextWebSocketFrame getBlockedQueryMessage() {
		return new TextWebSocketFrame("{\"type\":\"blocked\"}");
	}

	static TextWebSocketFrame getLockedQueryMessage() {
		return new TextWebSocketFrame("{\"type\":\"locked\"}");
	}

}
