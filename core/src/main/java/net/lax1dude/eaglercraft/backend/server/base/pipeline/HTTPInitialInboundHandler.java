package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.ReferenceCountUtil;
import net.lax1dude.eaglercraft.backend.server.adapter.PipelineAttributes;
import net.lax1dude.eaglercraft.backend.server.adapter.event.IEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.server.api.EnumPipelineEvent;
import net.lax1dude.eaglercraft.backend.server.base.EaglerListener;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataListener;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataSettings;

@ChannelHandler.Sharable
public class HTTPInitialInboundHandler extends ChannelInboundHandlerAdapter {

	public static final HTTPInitialInboundHandler INSTANCE = new HTTPInitialInboundHandler();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msgRaw) throws Exception {
		try {
			if(!ctx.channel().isActive()) {
				return;
			}
			NettyPipelineData pipelineData = ctx.channel().attr(PipelineAttributes.<NettyPipelineData>pipelineData()).get();
			if(!pipelineData.initStall && (msgRaw instanceof FullHttpRequest)) {
				FullHttpRequest msg = (FullHttpRequest) msgRaw;
				HttpHeaders headers = msg.headers();
				
				EaglerListener listener = pipelineData.listenerInfo;
				ConfigDataListener conf = listener.getConfigData();
				
				if(conf.isForwardSecret()) {
					if(!conf.getForwardSecretValue().equals(headers.get(conf.getForwardSecretHeader()))) {
						pipelineData.initStall = true;
						ctx.close();
						return;
					}
				}
				
				if(conf.isForwardIP()) {
					String forwardedIP = headers.get(conf.getForwardIPHeader());
					if(forwardedIP != null) {
						pipelineData.realAddress = forwardedIP;
					}else {
						pipelineData.connectionLogger.error(
								"Connected without a \"" + conf.getForwardIPHeader() + "\" header, disconnecting...");
						pipelineData.initStall = true;
						ctx.close();
						return;
					}
				}
				
				String connection = headers.get(HttpHeaderNames.CONNECTION);
				if(connection != null && "upgrade".equalsIgnoreCase(connection)) {
					String upgrade = headers.get(HttpHeaderNames.UPGRADE);
					if(upgrade != null && "websocket".equalsIgnoreCase(upgrade)) {
						pipelineData.initStall = true;
						handleWebSocket(ctx, pipelineData, msg);
						return;
					}
				}
				
				handleHTTP(ctx, pipelineData, msg);
			}else {
				ctx.close();
			}
		}finally {
			ReferenceCountUtil.release(msgRaw);
		}
	}

	private void handleWebSocket(ChannelHandlerContext ctx, NettyPipelineData pipelineData, FullHttpRequest msg) throws Exception {
		HttpHeaders headers = msg.headers();
		pipelineData.headerHost = headers.get(HttpHeaderNames.HOST);
		pipelineData.headerOrigin = headers.get(HttpHeaderNames.ORIGIN);
		pipelineData.headerUserAgent = headers.get(HttpHeaderNames.USER_AGENT);
		pipelineData.headerCookie = headers.get(HttpHeaderNames.COOKIE);
		pipelineData.headerAuthorization = headers.get(HttpHeaderNames.AUTHORIZATION);
		pipelineData.requestPath = msg.uri();
		
		ConfigDataSettings settings = pipelineData.server.getConfig().getSettings();
		ChannelPipeline pipeline = ctx.pipeline();
		pipeline.replace(PipelineTransformer.HANDLER_HTTP_AGGREGATOR, PipelineTransformer.HANDLER_WS_AGGREGATOR,
				new WebSocketFrameAggregator(settings.getHTTPWebSocketFragmentSize()));
		pipeline.replace(PipelineTransformer.HANDLER_HTTP_INITIAL, PipelineTransformer.HANDLER_WS_INITIAL,
				WebSocketInitialHandler.INSTANCE);
		pipeline.addBefore(PipelineTransformer.HANDLER_WS_INITIAL, PipelineTransformer.HANDLER_WS_PING,
				new WebSocketPingFrameHandler());
		
		IEventDispatchAdapter<?, ?> dispatch = pipelineData.server.eventDispatcher();
		msg.retain();
		dispatch.dispatchWebSocketOpenEvent(pipelineData, (evt, err) -> {
			ctx.channel().eventLoop().execute(() -> {
				try {
					if(err == null) {
						if(ctx.channel().isActive()) {
							if(!evt.isCancelled()) {
								handshakeWebSocket(ctx, pipelineData, msg, settings.getHTTPWebSocketMaxFrameLength());
							}else {
								ctx.close();
							}
						}
					}else {
						pipelineData.connectionLogger.error("Exception thrown while handling web socket open event", err);
						ctx.close();
					}
				}finally {
					msg.release();
				}
			});
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

	private void handleHTTP(ChannelHandlerContext ctx, NettyPipelineData pipelineData, FullHttpRequest msg) throws Exception {
		ChannelPipeline pipeline = ctx.pipeline();
		pipelineData.server.getPipelineTransformer().removeVanillaHandlers(pipeline);
		pipeline.addAfter(PipelineTransformer.HANDLER_HTTP_INITIAL, PipelineTransformer.HANDLER_HTTP,
				new HTTPRequestInboundHandler(pipelineData.server, pipelineData));
		pipeline.fireUserEventTriggered(EnumPipelineEvent.EAGLER_STATE_HTTP_REQUEST);
		ctx.fireChannelRead(ReferenceCountUtil.retain(msg));
		pipeline.remove(PipelineTransformer.HANDLER_HTTP_INITIAL);
	}

}
