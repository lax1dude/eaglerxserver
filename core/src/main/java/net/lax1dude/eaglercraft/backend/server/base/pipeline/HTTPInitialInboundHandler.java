package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.concurrent.GenericFutureListener;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataSettings;

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
			if(upgrade != null && "websocket".equalsIgnoreCase(upgrade)) {
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
		
		ConfigDataSettings settings = server.getConfig().getSettings();
		ctx.pipeline().replace(PipelineTransformer.HANDLER_HTTP_AGGREGATOR, PipelineTransformer.HANDLER_WS_AGGREGATOR,
				new WebSocketFrameAggregator(settings.getHTTPWebSocketFragmentSize()));
		ctx.pipeline().replace(PipelineTransformer.HANDLER_HTTP_INITIAL, PipelineTransformer.HANDLER_WS_INITIAL,
				new WebSocketInitialInboundHandler(server, pipelineData, retainWaitingOutbound(waitingOutboundFrames)));
		
		WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(
				(pipelineData.wss ? "wss://" : "ws://") + pipelineData.headerHost + pipelineData.requestPath, null,
				true, settings.getHTTPWebSocketMaxFrameLength());
		WebSocketServerHandshaker hs = factory.newHandshaker(msg);
		if(hs != null) {
			hs.handshake(ctx.channel(), msg).addListener((future) -> {
				if(future.isSuccess()) {
					//TODO handle websocket
				}else {
					ctx.channel().close();
				}
			});
		}else {
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel())
					.addListener(ChannelFutureListener.CLOSE);
		}
	}

	private void handleHTTP(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
		server.getPipelineTransformer().removeVanillaHandlers(ctx.pipeline());
		
		//TODO: handle http request
		
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

	private List<ByteBuf> retainWaitingOutbound(List<ByteBuf> buffers) {
		if (buffers != null) {
			List<ByteBuf> framesRet = new ArrayList<>(buffers.size());
			for (ByteBuf b : buffers) {
				framesRet.add(b.retain());
			}
			return framesRet;
		}else {
			return null;
		}
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
