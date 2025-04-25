/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtensionHandler;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtensionHandshaker;
import io.netty.handler.codec.http.websocketx.extensions.compression.DeflateFrameServerExtensionHandshaker;
import io.netty.handler.codec.http.websocketx.extensions.compression.PerMessageDeflateServerExtensionHandshaker;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.lax1dude.eaglercraft.backend.server.adapter.IPipelineComponent;
import net.lax1dude.eaglercraft.backend.server.adapter.IPipelineComponent.EnumPipelineComponent;
import net.lax1dude.eaglercraft.backend.server.api.EnumPlatformType;
import net.lax1dude.eaglercraft.backend.server.base.EaglerListener;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.ISSLContextProvider;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;
import net.lax1dude.eaglercraft.backend.server.base.RewindService;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataSettings;
import net.lax1dude.eaglercraft.backend.server.util.Util;

public class PipelineTransformer {

	public static final String HANDLER_MULTI_STACK_INITIAL = "eagler-multistack-initial";
	public static final String HANDLER_HAPROXY_DETECTION = "eagler-haproxy-detection";
	public static final String HANDLER_HTTP_SSL = "eagler-ssl-handler";
	public static final String HANDLER_HTTP_SERVER_CODEC = "eagler-http-codec";
	public static final String HANDLER_HTTP_AGGREGATOR = "eagler-http-aggregator";
	public static final String HANDLER_WS_AGGREGATOR = "eagler-ws-aggregator";
	public static final String HANDLER_WS_COMPRESSION = "eagler-ws-compression";
	public static final String HANDLER_HTTP_INITIAL = "eagler-http-initial";
	public static final String HANDLER_WS_INITIAL = "eagler-ws-initial";
	public static final String HANDLER_WS_PING = "eagler-ws-ping-handler";
	public static final String HANDLER_HANDSHAKE = "eagler-handshake";
	public static final String HANDLER_OUTBOUND_THROW = "eagler-outbound-throw";
	public static final String HANDLER_QUERY = "eagler-query";
	public static final String HANDLER_HTTP = "eagler-http";
	public static final String HANDLER_FRAME_CODEC = "eagler-frame-codec";
	public static final String HANDLER_REWIND_CODEC = "eagler-rewind-codec";
	public static final String HANDLER_REWIND_DECODER = "eagler-rewind-decoder";
	public static final String HANDLER_REWIND_ENCODER = "eagler-rewind-encoder";
	public static final String HANDLER_REWIND_INJECTOR = "eagler-rewind-injector";
	public static final String HANDLER_INJECTED = "eagler-v5-msg-handler";

	protected static final Set<EnumPipelineComponent> VANILLA_FRAME_DECODERS = EnumSet.of(
			EnumPipelineComponent.FRAME_DECODER, EnumPipelineComponent.FRAME_ENCODER,
			EnumPipelineComponent.BUKKIT_LEGACY_HANDLER, EnumPipelineComponent.BUNGEE_LEGACY_HANDLER,
			EnumPipelineComponent.BUNGEE_LEGACY_KICK_ENCODER, EnumPipelineComponent.VELOCITY_LEGACY_PING_ENCODER);

	private static final boolean SUPPORTS_COMPRESSION_FRAME = Util.classExists(
			"io.netty.handler.codec.http.websocketx.extensions.compression.DeflateFrameServerExtensionHandshaker");
	private static final boolean SUPPORTS_COMPRESSION_MESSAGE = Util.classExists(
			"io.netty.handler.codec.http.websocketx.extensions.compression.DeflateFrameServerExtensionHandshaker");

	private static final boolean NEW_AGGREGATOR_CTOR;

	static {
		boolean b = false;
		try {
			HttpObjectAggregator.class.getConstructor(int.class, boolean.class);
			b = true;
		}catch(ReflectiveOperationException ex) {
		}
		NEW_AGGREGATOR_CTOR = b;
	}

	public final EaglerXServer<?> server;
	public final RewindService<?> rewind;

	private final Object[] enabledExtensions;

	private Runnable nag;

	public PipelineTransformer(EaglerXServer<?> server, RewindService<?> rewind) {
		this.server = server;
		this.rewind = rewind;
		if(!SUPPORTS_COMPRESSION_FRAME && !SUPPORTS_COMPRESSION_MESSAGE) {
			if(server.getPlatformType() == EnumPlatformType.BUKKIT) {
				nag = () -> {
					server.logger().error("WebSocket compression is not supported by the Netty bundled with this version of Bukkit");
					server.logger().error("Excessive bandwidth will be used, and you will have a bad time trying to play on wireless devices");
					server.logger().error("Please try a newer version of Bukkit (preferably Paper) such as 1.12.2 to fix this issue");
					server.logger().error("Alternatively, you can add a newer version of Netty to the classpath as a JAR file using java -cp");
					server.logger().error("You have been warned, don't be a fool");
				};
				nag.run();
			}else {
				server.logger().error("WebSocket compression is not supported on this platform");
			}
			enabledExtensions = null;
		}else {
			int compressionLevel = Math.min(server.getConfig().getSettings().getHTTPWebSocketCompressionLevel(), 9);
			if(compressionLevel > 0) {
				List<WebSocketServerExtensionHandshaker> extensions = new ArrayList<>();
				if(SUPPORTS_COMPRESSION_FRAME) {
					extensions.add(new DeflateFrameServerExtensionHandshaker(compressionLevel));
				}
				if(SUPPORTS_COMPRESSION_MESSAGE) {
					extensions.add(new PerMessageDeflateServerExtensionHandshaker(
							compressionLevel, ZlibCodecFactory.isSupportingWindowSizeAndMemLevel(),
							PerMessageDeflateServerExtensionHandshaker.MAX_WINDOW_SIZE, false, false));
				}
				enabledExtensions = extensions.toArray(new WebSocketServerExtensionHandshaker[extensions.size()]);
			}else {
				enabledExtensions = null;
			}
		}
	}

	public void nagAgain() {
		if(nag != null) {
			nag.run();
			server.logger().error("(Warning has been repeated in case you don't know how to scroll up)");
			nag = null;
		}
	}

	public void injectSingleStack(List<IPipelineComponent> components, Channel channel, NettyPipelineData pipelineData) {
		ChannelPipeline pipeline = channel.pipeline();
		String before = null;
		String first = null;
		boolean e = false;
		boolean haproxy = false;
		String bungeeHack = null;
		for(IPipelineComponent comp : components) {
			if(VANILLA_FRAME_DECODERS.contains(comp.getIdentifiedType())) {
				pipeline.remove(comp.getHandle());
				if(comp.getIdentifiedType() == EnumPipelineComponent.BUNGEE_LEGACY_KICK_ENCODER) {
					bungeeHack = comp.getName();
				}
			}else {
				if(!e) {
					if(comp.getIdentifiedType() != EnumPipelineComponent.HAPROXY_HANDLER) {
						first = before;
						e = true;
					}else {
						haproxy = true;
					}
					before = comp.getName();
				}
			}
		}
		if(bungeeHack != null) {
			pipeline.addLast(bungeeHack, NOPDummyHandler.INSTANCE);
		}
		if(!e) {
			return;
		}
		EaglerListener eagListener = pipelineData.listenerInfo;
		if(eagListener.isTLSEnabled()) {
			ISSLContextProvider ssl = eagListener.getSSLContext();
			if(ssl == null) {
				throw new IllegalStateException();
			}
			if(!eagListener.isTLSRequired()) {
				MultiStackInitialInboundHandler ms = new MultiStackInitialInboundHandler(this, pipelineData, null, null);
				if(first == null) {
					channel.pipeline().addFirst(HANDLER_MULTI_STACK_INITIAL, ms);
				}else {
					channel.pipeline().addAfter(first, HANDLER_MULTI_STACK_INITIAL, ms);
				}
			}else {
				initializeHTTPHandler(pipelineData, ssl, pipeline, first);
			}
		}else {
			initializeHTTPHandler(pipelineData, null, pipeline, first);
		}
		if(haproxy && eagListener.getConfigData().isDualStackHAProxyDetection()) {
			channel.pipeline().addFirst(HAProxyDetectionHandler.INSTANCE);
		}
		channel.pipeline().addLast(HANDLER_OUTBOUND_THROW, OutboundPacketThrowHandler.INSTANCE);
	}

	public void injectDualStack(List<IPipelineComponent> components, Channel channel, NettyPipelineData pipelineData) {
		List<ChannelHandler> toRemove = new ArrayList<>(4);
		String first = null;
		String bungeeHack = null;
		boolean haproxy = false;
		for(IPipelineComponent comp : components) {
			if(VANILLA_FRAME_DECODERS.contains(comp.getIdentifiedType())) {
				toRemove.add(comp.getHandle());
				if(comp.getIdentifiedType() == EnumPipelineComponent.BUNGEE_LEGACY_KICK_ENCODER) {
					bungeeHack = comp.getName();
				}
			}else {
				if(first == null) {
					if(comp.getIdentifiedType() != EnumPipelineComponent.HAPROXY_HANDLER) {
						first = comp.getName();
					}else {
						haproxy = true;
					}
				}
			}
		}
		if(first == null) {
			return;
		}
		channel.pipeline().addBefore(first, HANDLER_MULTI_STACK_INITIAL, new MultiStackInitialInboundHandler(this, pipelineData, toRemove, bungeeHack));
		EaglerListener eagListener = pipelineData.listenerInfo;
		if(haproxy && eagListener.getConfigData().isDualStackHAProxyDetection()) {
			channel.pipeline().addFirst(HAProxyDetectionHandler.INSTANCE);
		}
		channel.pipeline().addLast(HANDLER_OUTBOUND_THROW, OutboundPacketThrowHandler.INSTANCE);
	}

	protected void initializeHTTPHandler(NettyPipelineData pipelineData, ISSLContextProvider context, ChannelPipeline pipeline,
			String after) {
		if(context != null) {
			SslHandler sslHandler = context.newHandler(pipeline.channel().alloc());
			if(sslHandler == null) {
				pipeline.channel().close();
				return;
			}
			if(after == null) {
				pipeline.addFirst(HANDLER_HTTP_SSL, sslHandler);
			}else {
				pipeline.addAfter(after, HANDLER_HTTP_SSL, sslHandler);
			}
			after = HANDLER_HTTP_SSL;
			pipelineData.wss = true;
		}
		ConfigDataSettings settings = server.getConfig().getSettings();
		HttpServerCodec serverCodec = new HttpServerCodec(settings.getHTTPMaxInitialLineLength(), 
				settings.getHTTPMaxHeaderSize(), settings.getHTTPMaxChunkSize());
		if(after == null) {
			pipeline.addFirst(HANDLER_HTTP_SERVER_CODEC, serverCodec);
		}else {
			pipeline.addAfter(after, HANDLER_HTTP_SERVER_CODEC, serverCodec);
		}
		after = HANDLER_HTTP_SERVER_CODEC;
		HttpObjectAggregator ag;
		if(NEW_AGGREGATOR_CTOR) {
			ag = new HttpObjectAggregator(settings.getHTTPMaxContentLength(), true);
		}else {
			ag = new HttpObjectAggregator(settings.getHTTPMaxContentLength());
		}
		pipeline.addAfter(after, HANDLER_HTTP_AGGREGATOR, ag);
		after = HANDLER_HTTP_AGGREGATOR;
		if(enabledExtensions != null) {
			pipeline.addAfter(after, HANDLER_WS_COMPRESSION,
					new WebSocketServerExtensionHandler((WebSocketServerExtensionHandshaker[]) enabledExtensions));
			after = HANDLER_WS_COMPRESSION;
		}
		pipeline.addAfter(after, HANDLER_HTTP_INITIAL, HTTPInitialInboundHandler.INSTANCE);
	}

	protected void removeVanillaHandlers(ChannelPipeline pipeline) {
		Iterator<String> keyItr = pipeline.names().iterator();
		while(keyItr.hasNext()) {
			String nm = keyItr.next();
			if(PipelineTransformer.HANDLER_HTTP_INITIAL.equals(nm) || PipelineTransformer.HANDLER_WS_INITIAL.equals(nm)) {
				while(keyItr.hasNext()) {
					nm = keyItr.next();
					ChannelHandler handler = pipeline.get(nm);
					if (!(handler instanceof ReadTimeoutHandler)) {
						try {
							pipeline.remove(nm);
						}catch(NoSuchElementException ex) {
						}
					}
				}
			}
		}
	}

}
