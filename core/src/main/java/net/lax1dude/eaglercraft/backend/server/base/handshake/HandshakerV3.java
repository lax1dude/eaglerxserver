package net.lax1dude.eaglercraft.backend.server.base.handshake;

import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.WebSocketEaglerInitialHandler;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public class HandshakerV3 extends HandshakerV2 {

	public HandshakerV3(EaglerXServer<?> server, NettyPipelineData pipelineData,
			WebSocketEaglerInitialHandler inboundHandler) {
		super(server, pipelineData, inboundHandler);
	}

	public void init(ChannelHandlerContext ctx, String eaglerBrand, String eaglerVersionString,
			int minecraftVersion, boolean auth, byte[] authUsername) {
		handlePacketInit(ctx, eaglerBrand, eaglerVersionString, minecraftVersion, auth, authUsername);
	}

	@Override
	protected int getVersion() {
		return 3;
	}

	@Override
	protected GamePluginMessageProtocol getFinalVersion() {
		return GamePluginMessageProtocol.V3;
	}

	@Override
	protected ChannelFuture sendPacketDenyLogin(ChannelHandlerContext ctx, String message) {
		if(message.length() > 65535) {
			message = message.substring(0, 65535);
		}
		ByteBuf buffer = ctx.alloc().buffer();
		buffer.writeByte(HandshakePacketTypes.PROTOCOL_SERVER_DENY_LOGIN);
		byte[] msg = message.getBytes(StandardCharsets.UTF_8);
		int len = msg.length;
		if(len > 65535) {
			len = 65535;
		}
		buffer.writeShort(len);
		buffer.writeBytes(msg, 0, len);
		return ctx.writeAndFlush(new BinaryWebSocketFrame(buffer));
	}

}
