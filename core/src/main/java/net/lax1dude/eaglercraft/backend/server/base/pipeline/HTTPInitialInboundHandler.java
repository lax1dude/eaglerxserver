package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.ReferenceCountUtil;
import net.lax1dude.eaglercraft.backend.server.adapter.PipelineAttributes;
import net.lax1dude.eaglercraft.backend.server.adapter.event.IEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataSettings;

@ChannelHandler.Sharable
public class HTTPInitialInboundHandler extends ChannelInboundHandlerAdapter {

	public static final HTTPInitialInboundHandler INSTANCE = new HTTPInitialInboundHandler();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msgRaw) throws Exception {
		try {
			if(msgRaw instanceof FullHttpRequest) {
				FullHttpRequest msg = (FullHttpRequest) msgRaw;
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
			}else {
				ctx.close();
			}
		}finally {
			ReferenceCountUtil.release(msgRaw);
		}
	}

	private void handleWebSocket(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
		NettyPipelineData pipelineData = ctx.channel().attr(PipelineAttributes.<NettyPipelineData>pipelineData()).get();
		HttpHeaders headers = msg.headers();
		pipelineData.headerHost = headers.get(HttpHeaderNames.HOST);
		pipelineData.headerOrigin = headers.get(HttpHeaderNames.ORIGIN);
		pipelineData.headerUserAgent = headers.get(HttpHeaderNames.USER_AGENT);
		pipelineData.headerCookie = headers.get(HttpHeaderNames.COOKIE);
		pipelineData.headerAuthorization = headers.get(HttpHeaderNames.AUTHORIZATION);
		pipelineData.requestPath = msg.uri();
		
		ConfigDataSettings settings = pipelineData.server.getConfig().getSettings();
		ctx.pipeline().replace(PipelineTransformer.HANDLER_HTTP_AGGREGATOR, PipelineTransformer.HANDLER_WS_AGGREGATOR,
				new WebSocketFrameAggregator(settings.getHTTPWebSocketFragmentSize()));
		ctx.pipeline().replace(PipelineTransformer.HANDLER_HTTP_INITIAL, PipelineTransformer.HANDLER_WS_INITIAL,
				WebSocketInitialHandler.INSTANCE);
		ctx.pipeline().addBefore(PipelineTransformer.HANDLER_WS_INITIAL, PipelineTransformer.HANDLER_WS_PING,
				new WebSocketPingFrameHandler());
		
		IEventDispatchAdapter<?, ?> dispatch = pipelineData.server.eventDispatcher();
		msg.retain();
		dispatch.dispatchWebSocketOpenEvent(pipelineData, (evt, err) -> {
			try {
				if(err == null) {
					handshakeWebSocket(ctx, pipelineData, msg, settings.getHTTPWebSocketMaxFrameLength());
				}else {
					pipelineData.connectionLogger.error("Exception thrown while handling web socket open event", err);
					ctx.close();
				}
			}finally {
				msg.release();
			}
		});
	}

	private void handshakeWebSocket(ChannelHandlerContext ctx, NettyPipelineData pipelineData, FullHttpRequest msg, int maxFrameLen) {
		WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(
				(pipelineData.wss ? "wss://" : "ws://") + pipelineData.headerHost + pipelineData.requestPath, null,
				true, maxFrameLen);
		WebSocketServerHandshaker hs = factory.newHandshaker(msg);
		if(hs != null) {
			hs.handshake(ctx.channel(), msg).addListener((future) -> {
				if(future.isSuccess()) {
					pipelineData.scheduleLoginTimeoutHelper();
				}else {
					ctx.close();
				}
			});
		}else {
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel())
					.addListener(ChannelFutureListener.CLOSE);
		}
	}

	private void handleHTTP(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
		NettyPipelineData pipelineData = ctx.channel().attr(PipelineAttributes.<NettyPipelineData>pipelineData()).get();
		pipelineData.server.getPipelineTransformer().removeVanillaHandlers(ctx.pipeline());
		ctx.pipeline().addAfter(PipelineTransformer.HANDLER_HTTP_INITIAL, PipelineTransformer.HANDLER_HTTP,
				new HTTPRequestInboundHandler(pipelineData.server, pipelineData));
		ctx.fireChannelRead(ReferenceCountUtil.retain(msg));
		ctx.pipeline().remove(PipelineTransformer.HANDLER_HTTP_INITIAL);
	}

}
