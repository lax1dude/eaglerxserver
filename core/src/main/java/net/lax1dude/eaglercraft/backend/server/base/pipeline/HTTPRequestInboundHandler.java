package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;

public class HTTPRequestInboundHandler extends ChannelInboundHandlerAdapter {

	private final EaglerXServer<?> server;
	private final NettyPipelineData pipelineData;

	public HTTPRequestInboundHandler(EaglerXServer<?> server, NettyPipelineData pipelineData) {
		this.server = server;
		this.pipelineData = pipelineData;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msgRaw) throws Exception {
		try {
			if(msgRaw instanceof HttpRequest) {
				//TODO
			}
		}finally {
			ReferenceCountUtil.release(msgRaw);
		}
	}

}
