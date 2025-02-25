package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import io.netty.handler.codec.haproxy.HAProxyMessageEncoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtensionHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.DeflateFrameServerExtensionHandshaker;
import io.netty.handler.codec.http.websocketx.extensions.compression.PerMessageDeflateServerExtensionHandshaker;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.lax1dude.eaglercraft.backend.server.adapter.IPipelineComponent;
import net.lax1dude.eaglercraft.backend.server.adapter.IPipelineComponent.EnumPipelineComponent;
import net.lax1dude.eaglercraft.backend.server.base.EaglerListener;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.ISSLContextProvider;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;
import net.lax1dude.eaglercraft.backend.server.base.RewindService;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataSettings;

public class PipelineTransformer {

	public static final String HANDLER_MULTI_STACK_INITIAL = "eagler-multistack-initial";
	public static final String HANDLER_HTTP_SSL = "eagler-ssl-handler";
	public static final String HANDLER_HTTP_SERVER_CODEC = "eagler-http-codec";
	public static final String HANDLER_HTTP_AGGREGATOR = "eagler-http-aggregator";
	public static final String HANDLER_WS_AGGREGATOR = "eagler-ws-aggregator";
	public static final String HANDLER_WS_COMPRESSION = "eagler-ws-compression";
	public static final String HANDLER_HTTP_INITIAL = "eagler-http-initial";
	public static final String HANDLER_WS_INITIAL = "eagler-ws-initial";
	public static final String HANDLER_QUERY_HANDLER = "eagler-query-handler";
	public static final String HANDLER_FRAME_DECODER = "eagler-frame-decoder";
	public static final String HANDLER_FRAME_ENCODER = "eagler-frame-encoder";
	public static final String HANDLER_REWIND_CODEC = "eagler-rewind-codec";
	public static final String HANDLER_REWIND_DECODER = "eagler-rewind-decoder";
	public static final String HANDLER_REWIND_ENCODER = "eagler-rewind-encoder";

	protected static final Set<String> EAGLER_HTTP_HANDLERS = ImmutableSet.of(HANDLER_HTTP_SSL,
			HANDLER_HTTP_SERVER_CODEC, HANDLER_HTTP_AGGREGATOR, HANDLER_WS_AGGREGATOR, HANDLER_WS_COMPRESSION,
			HANDLER_HTTP_INITIAL);

	protected static final Set<EnumPipelineComponent> VANILLA_FRAME_DECODERS = EnumSet.of(
			EnumPipelineComponent.FRAME_DECODER, EnumPipelineComponent.FRAME_ENCODER,
			EnumPipelineComponent.BUKKIT_LEGACY_HANDLER, EnumPipelineComponent.BUNGEE_LEGACY_HANDLER,
			EnumPipelineComponent.BUNGEE_LEGACY_KICK_ENCODER, EnumPipelineComponent.VELOCITY_LEGACY_PING_ENCODER);

	public final EaglerXServer<?> server;
	public final RewindService<?> rewind;

	public PipelineTransformer(EaglerXServer<?> server, RewindService<?> rewind) {
		this.server = server;
		this.rewind = rewind;
	}

	public void injectSingleStack(List<IPipelineComponent> components, Channel channel, NettyPipelineData pipelineData) {
		ChannelPipeline pipeline = channel.pipeline();
		String first = null;
		for(IPipelineComponent comp : components) {
			if(VANILLA_FRAME_DECODERS.contains(comp.getIdentifiedType())) {
				pipeline.remove(comp.getHandle());
			}else {
				if(first == null && comp.getIdentifiedType() != EnumPipelineComponent.HAPROXY_HANDLER) {
					first = comp.getName();
				}
			}
		}
		if(first == null) {
			return;
		}
		EaglerListener eagListener = pipelineData.listenerInfo;
		if(eagListener.isTLSEnabled()) {
			ISSLContextProvider ssl = eagListener.getSSLContext();
			if(ssl == null) {
				throw new IllegalStateException();
			}
			if(!eagListener.isTLSRequired()) {
				channel.pipeline().addBefore(first, HANDLER_MULTI_STACK_INITIAL, new MultiStackInitialInboundHandler(this, pipelineData, null));
			}else {
				initializeHTTPHandler(pipelineData, ssl, pipeline, first, null);
			}
		}else {
			initializeHTTPHandler(pipelineData, null, pipeline, first, null);
		}
	}

	public void injectDualStack(List<IPipelineComponent> components, Channel channel, NettyPipelineData pipelineData) {
		List<ChannelHandler> toRemove = new ArrayList<>(4);
		String first = null;
		for(IPipelineComponent comp : components) {
			if(VANILLA_FRAME_DECODERS.contains(comp.getIdentifiedType())) {
				toRemove.add(comp.getHandle());
			}else {
				if(first == null && comp.getIdentifiedType() != EnumPipelineComponent.HAPROXY_HANDLER) {
					first = comp.getName();
				}
			}
		}
		if(first == null) {
			return;
		}
		channel.pipeline().addBefore(first, HANDLER_MULTI_STACK_INITIAL, new MultiStackInitialInboundHandler(this, pipelineData, toRemove));
	}

	protected void initializeHTTPHandler(NettyPipelineData pipelineData, ISSLContextProvider context, ChannelPipeline pipeline,
			String before, List<ByteBuf> waitingOutboundFrames) {
		if(context != null) {
			SslHandler sslHandler = context.newHandler(pipeline.channel().alloc());
			if(sslHandler == null) {
				pipeline.channel().close();
				return;
			}
			pipeline.addBefore(before, HANDLER_HTTP_SSL, sslHandler);
			pipelineData.wss = true;
		}
		ConfigDataSettings settings = server.getConfig().getSettings();
		pipeline.addBefore(before, HANDLER_HTTP_SERVER_CODEC, new HttpServerCodec(settings.getHTTPMaxInitialLineLength(), 
				settings.getHTTPMaxHeaderSize(), settings.getHTTPMaxChunkSize()));
		pipeline.addBefore(before, HANDLER_HTTP_AGGREGATOR, new HttpObjectAggregator(settings.getHTTPMaxContentLength(), true));
		int compressionLevel = Math.min(settings.getHTTPWebSocketCompressionLevel(), 9);
		if(compressionLevel > 0) {
			DeflateFrameServerExtensionHandshaker deflateExtensionHandshaker = new DeflateFrameServerExtensionHandshaker(compressionLevel);
			PerMessageDeflateServerExtensionHandshaker perMessageDeflateExtensionHandshaker = new PerMessageDeflateServerExtensionHandshaker(
					compressionLevel, ZlibCodecFactory.isSupportingWindowSizeAndMemLevel(),
					PerMessageDeflateServerExtensionHandshaker.MAX_WINDOW_SIZE, false, false);
			pipeline.addBefore(before, HANDLER_WS_COMPRESSION, new WebSocketServerExtensionHandler(
					deflateExtensionHandshaker, perMessageDeflateExtensionHandshaker));
		}
		List<ByteBuf> waitingOut;
		if(waitingOutboundFrames != null) {
			waitingOut = new ArrayList<>(waitingOutboundFrames);
			for(ByteBuf buf : waitingOut) {
				buf.retain();
			}
		}else {
			waitingOut = new ArrayList<>(4);
		}
		pipeline.addBefore(before, HANDLER_HTTP_INITIAL, new HTTPInitialInboundHandler(server, pipelineData, waitingOut));
	}

	protected void removeVanillaHandlers(ChannelPipeline pipeline) {
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

}
