package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

import com.google.common.net.InetAddresses;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.ReferenceCountUtil;
import net.lax1dude.eaglercraft.backend.server.adapter.PipelineAttributes;
import net.lax1dude.eaglercraft.backend.server.adapter.event.IEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.server.api.EnumPipelineEvent;
import net.lax1dude.eaglercraft.backend.server.base.CompoundRateLimiterMap;
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
			if(!pipelineData.initStall && (msgRaw instanceof FullHttpRequest msg)) {
				if(HTTPMessageUtils.getProtocolVersion(msg) != HttpVersion.HTTP_1_1) {
					pipelineData.initStall = true;
					ctx.close();
					return;
				}
				
				HttpHeaders headers = msg.headers();
				
				EaglerListener listener = pipelineData.listenerInfo;
				ConfigDataListener conf = listener.getConfigData();
				
				if(conf.isForwardSecret()) {
					if(!conf.getForwardSecretValue().equals(headers.get(conf.getForwardSecretHeader()))) {
						pipelineData.connectionLogger.error("Connected without a valid forwarding secret header, disconnecting...");
						pipelineData.initStall = true;
						ctx.close();
						return;
					}
				}
				
				if(conf.isForwardIP()) {
					List<String> forwardedIP = headers.getAll(conf.getForwardIPHeader());
					if(forwardedIP != null && !forwardedIP.isEmpty()) {
						pipelineData.realAddress = forwardedIP.get(0);
						CompoundRateLimiterMap rateLimiter = pipelineData.listenerInfo.getRateLimiter();
						if(rateLimiter != null) {
							InetAddress addr;
							try {
								addr = InetAddresses.forString(pipelineData.realAddress);
							}catch(IllegalArgumentException ex) {
								pipelineData.connectionLogger.error("Connected with an invalid \""
										+ conf.getForwardIPHeader() + "\" header, disconnecting...", ex);
								pipelineData.initStall = true;
								ctx.close();
								return;
							}
							pipelineData.realInetAddress = addr;
							if((pipelineData.rateLimits = rateLimiter.rateLimit(addr)) == null) {
								pipelineData.initStall = true;
								ctx.close();
								return;
							}
						}
					}else {
						pipelineData.connectionLogger.error(
								"Connected without a \"" + conf.getForwardIPHeader() + "\" header, disconnecting...");
						pipelineData.initStall = true;
						ctx.close();
						return;
					}
				}
				
				String connection = headers.get("connection");
				if(connection != null && "upgrade".equalsIgnoreCase(connection)) {
					String upgrade = headers.get("upgrade");
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
		}catch(Throwable t) {
			t.printStackTrace();
		}finally {
			ReferenceCountUtil.release(msgRaw);
		}
	}

	private void handleWebSocket(ChannelHandlerContext ctx, NettyPipelineData pipelineData, FullHttpRequest msg) throws Exception {
		HttpHeaders headers = msg.headers();
		pipelineData.headerHost = headers.get("host");
		pipelineData.headerOrigin = headers.get("origin");
		pipelineData.headerUserAgent = headers.get("user-agent");
		pipelineData.headerCookie = headers.get("cookie");
		pipelineData.headerAuthorization = headers.get("authorization");
		pipelineData.requestPath = HTTPMessageUtils.getURI(msg);
		
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
		dispatch.dispatchWebSocketOpenEvent(pipelineData, msg, (evt, err) -> {
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
					pipelineData.initStall = false;
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
		pipeline.addLast(PipelineTransformer.HANDLER_HTTP,
				new HTTPRequestInboundHandler(pipelineData.server, pipelineData));
		pipeline.fireUserEventTriggered(EnumPipelineEvent.EAGLER_STATE_HTTP_REQUEST);
		ctx.fireChannelRead(ReferenceCountUtil.retain(msg));
		pipeline.remove(PipelineTransformer.HANDLER_HTTP_INITIAL);
	}

	static boolean recheckRatelimitAddress(ChannelHandlerContext ctx, NettyPipelineData pipelineData, FullHttpRequest msg) {
		EaglerListener listener = pipelineData.listenerInfo;
		ConfigDataListener conf = listener.getConfigData();
		HttpHeaders headers = msg.headers();
		if(conf.isForwardSecret()) {
			if(!conf.getForwardSecretValue().equals(headers.get(conf.getForwardSecretHeader()))) {
				pipelineData.connectionLogger.error("Connected without a valid forwarding secret header, disconnecting...");
				return false;
			}
		}
		if(conf.isForwardIP()) {
			List<String> forwardedIP = headers.getAll(conf.getForwardIPHeader());
			if(forwardedIP != null && !forwardedIP.isEmpty()) {
				pipelineData.realAddress = forwardedIP.get(0);
				CompoundRateLimiterMap rateLimiter = pipelineData.listenerInfo.getRateLimiter();
				if(rateLimiter != null) {
					InetAddress addr;
					try {
						addr = InetAddresses.forString(pipelineData.realAddress);
					}catch(IllegalArgumentException ex) {
						pipelineData.connectionLogger.error("Connected with an invalid \""
								+ conf.getForwardIPHeader() + "\" header, disconnecting...", ex);
						return false;
					}
					pipelineData.realInetAddress = addr;
					pipelineData.rateLimits = rateLimiter.getRateLimit(addr);
				}
				return true;
			}else {
				pipelineData.connectionLogger.error(
						"Connected without a \"" + conf.getForwardIPHeader() + "\" header, disconnecting...");
				return false;
			}
		}else {
			CompoundRateLimiterMap rateLimiter = pipelineData.listenerInfo.getRateLimiter();
			if(rateLimiter != null) {
				SocketAddress addr = ctx.channel().remoteAddress();
				if(addr instanceof InetSocketAddress inetAddr) {
					pipelineData.rateLimits = rateLimiter.getRateLimit(inetAddr.getAddress());
				}else {
					pipelineData.connectionLogger.warn("Unable to ratelimit unknown address type: " + addr.getClass().getName()
							+ " - \"" + addr + "\"");
				}
			}
			return true;
		}
	}

}
