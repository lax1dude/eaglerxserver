package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;

public class WebSocketInitialInboundHandler extends MessageToMessageCodec<WebSocketFrame, ByteBuf> {

	private final EaglerXServer<?> server;
	private final NettyPipelineData pipelineData;
	private List<ByteBuf> waitingOutboundFrames;

	public WebSocketInitialInboundHandler(EaglerXServer<?> server, NettyPipelineData pipelineData, List<ByteBuf> waitingOutboundFrames) {
		this.server = server;
		this.pipelineData = pipelineData;
		this.waitingOutboundFrames = waitingOutboundFrames;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> output) throws Exception {
		
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> output) throws Exception {
		if (ctx.channel().isActive()) {
			if(waitingOutboundFrames == null) {
				waitingOutboundFrames = new ArrayList<>(4);
			}
			waitingOutboundFrames.add(msg.retain());
		}
		output.add(Unpooled.EMPTY_BUFFER); // :(
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		release();
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		super.handlerRemoved(ctx);
		release();
	}

	private void release() {
		if (waitingOutboundFrames != null) {
			for (ByteBuf b : waitingOutboundFrames) {
				b.release();
			}
			waitingOutboundFrames = null;
		}
	}

}
