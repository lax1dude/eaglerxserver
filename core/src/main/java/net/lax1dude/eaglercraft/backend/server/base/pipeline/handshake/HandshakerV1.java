package net.lax1dude.eaglercraft.backend.server.base.pipeline.handshake;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.WebSocketInitialInboundHandler;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.WebSocketInitialInboundHandler.IHandshaker;

public class HandshakerV1 extends HandshakerInstance implements IHandshaker {

	public HandshakerV1(WebSocketInitialInboundHandler inboundHandler) {
		super(inboundHandler);
	}

	public HandshakerV1 init(ChannelHandlerContext ctx, String eaglerBrand, String eaglerVersionString) {
		handlePacketInit(ctx, eaglerBrand, eaglerVersionString, 47, false, null);
		return this;
	}

	@Override
	public void handleInbound(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {
		
	}

	@Override
	public boolean handleOutbound(ChannelHandlerContext ctx, ByteBuf buffer) {
		return false;
	}

}
