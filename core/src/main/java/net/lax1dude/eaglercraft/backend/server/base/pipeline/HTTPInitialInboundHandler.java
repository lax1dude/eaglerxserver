package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import io.netty.handler.codec.haproxy.HAProxyMessageEncoder;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;

public class HTTPInitialInboundHandler extends MessageToMessageCodec<HttpRequest, ByteBuf> {

	private final EaglerXServer<?> server;
	private final NettyPipelineData pipelineData;
	private List<ByteBuf> waitingOutboundFrames;

	public HTTPInitialInboundHandler(EaglerXServer<?> server, NettyPipelineData pipelineData, List<ByteBuf> waitingOutboundFrames) {
		this.server = server;
		this.pipelineData = pipelineData;
		this.waitingOutboundFrames = waitingOutboundFrames;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, HttpRequest msg, List<Object> output) throws Exception {
		HttpHeaders headers = msg.headers();
		String connection = headers.get(HttpHeaderNames.CONNECTION);
		if(connection != null && "upgrade".equalsIgnoreCase(connection)) {
			String upgrade = headers.get(HttpHeaderNames.UPGRADE);
			if("websocket".equalsIgnoreCase(upgrade)) {
				handleWebSocket(ctx, msg);
				return;
			}
		}
		handleHTTP(ctx, msg);
	}

	private void handleWebSocket(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
		HttpHeaders headers = msg.headers();
		pipelineData.headerHost = headers.get(HttpHeaderNames.HOST);
		pipelineData.headerOrigin = headers.get(HttpHeaderNames.ORIGIN);
		pipelineData.headerUserAgent = headers.get(HttpHeaderNames.USER_AGENT);
		pipelineData.headerCookie = headers.get(HttpHeaderNames.COOKIE);
		pipelineData.headerAuthorization = headers.get(HttpHeaderNames.AUTHORIZATION);
		pipelineData.requestPath = msg.uri();
		
	}

	private void handleHTTP(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
		removeVanillaHandlers(ctx);
		
		//TODO: handle http request
		
	}

	private void removeVanillaHandlers(ChannelHandlerContext ctx) {
		ChannelPipeline pipeline = ctx.channel().pipeline();
		for(String key : pipeline.names()) {
			if(!PipelineTransformer.EAGLER_HTTP_HANDLERS.contains(key)) {
				ChannelHandler handler = pipeline.get(key);
				if (!(handler instanceof ReadTimeoutHandler) && !(handler instanceof HAProxyMessageDecoder)
						&& !(handler instanceof HAProxyMessageEncoder)) {
					pipeline.remove(key);
				}
			}
		}
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
