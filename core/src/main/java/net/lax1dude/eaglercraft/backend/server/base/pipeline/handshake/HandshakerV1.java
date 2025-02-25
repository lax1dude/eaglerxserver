package net.lax1dude.eaglercraft.backend.server.base.pipeline.handshake;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.WebSocketInitialInboundHandler;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.WebSocketInitialInboundHandler.IHandshaker;

public class HandshakerV1 implements IHandshaker {

	private final WebSocketInitialInboundHandler inboundHandler;

	public HandshakerV1(WebSocketInitialInboundHandler inboundHandler) {
		this.inboundHandler = inboundHandler;
	}

	public HandshakerV1 init(String eaglerBrand, String eaglerVersionString) {
		return this;
	}

	@Override
	public void handleInbound(ChannelHandlerContext ctx, ByteBuf buffer) {
		
	}

	@Override
	public boolean handleOutbound(ChannelHandlerContext ctx, ByteBuf buffer) {
		return false;
	}

}
