package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;

public class WebSocketQueryHandler extends ChannelInboundHandlerAdapter {

	private final EaglerXServer<?> server;
	private final NettyPipelineData pipelineData;

	public WebSocketQueryHandler(EaglerXServer<?> server, NettyPipelineData pipelineData) {
		this.server = server;
		this.pipelineData = pipelineData;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			
		}finally {
			ReferenceCountUtil.release(msg);
		}
	}

}
