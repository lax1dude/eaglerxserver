package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import com.google.gson.JsonObject;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.ReferenceCountUtil;
import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeKey;
import net.lax1dude.eaglercraft.backend.server.api.query.IQueryConnection;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.MOTDConnectionWrapper;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;
import net.lax1dude.eaglercraft.backend.server.util.Util;

public class WebSocketQueryHandler extends ChannelInboundHandlerAdapter implements IQueryConnection {

	private final EaglerXServer<?> server;
	private final NettyPipelineData pipelineData;
	private final long createdAt;

	private boolean handled = false;
	private volatile boolean dead = false;
	private String accepted = null;
	private Consumer<String> stringHandler = null;
	private Consumer<byte[]> binaryHandler = null;
	private long maxAge = -1l;

	private final ChannelFutureListener writeListener = (e) -> {
		WebSocketQueryHandler.this.checkClose();
	};

	private final AtomicInteger waitingPromiseCount = new AtomicInteger(0);

	public WebSocketQueryHandler(EaglerXServer<?> server, NettyPipelineData pipelineData) {
		this.server = server;
		this.pipelineData = pipelineData;
		this.createdAt = Util.steadyTime();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			if(msg instanceof CloseWebSocketFrame) {
				ctx.close();
			}else {
				if(!handled) {
					handled = true;
					if(msg instanceof TextWebSocketFrame) {
						String accept = ((TextWebSocketFrame)msg).text();
						if(accept.length() < 128) {
							accept = accept.toLowerCase();
							if(accept.startsWith("accept: ")) {
								accept = accept.substring(8).trim();
								if(accept.length() > 0) {
									if("motd".equals(accept) || accept.startsWith("motd.")) {
										acceptMOTD(ctx, accept);
										return;
									}else {
										acceptQuery(ctx, accept);
										return;
									}
								}
							}
						}
					}
					close();
				}else {
					if(msg instanceof TextWebSocketFrame) {
						if(stringHandler != null) {
							stringHandler.accept(((TextWebSocketFrame)msg).text());
						}
					}else if(msg instanceof BinaryWebSocketFrame) {
						if(binaryHandler != null) {
							ByteBuf buf = ((BinaryWebSocketFrame)msg).content();
							byte[] data = new byte[buf.readableBytes()];
							buf.readBytes(data);
							binaryHandler.accept(data);
						}
					}
				}
			}
		}finally {
			ReferenceCountUtil.release(msg);
		}
	}

	private void acceptMOTD(ChannelHandlerContext ctx, String type) {
		if(pipelineData.listenerInfo.isAllowMOTD()) {
			accepted = type;
			MOTDConnectionWrapper motdConnection = new MOTDConnectionWrapper(this);
			server.eventDispatcher().dispatchMOTDEvent(motdConnection, (motdEvent, err) -> {
				try {
					if(err != null) {
						maxAge = -1l;
						pipelineData.connectionLogger.error("MOTD event handler raised an exception", err);
					}
				}finally {
					if(maxAge <= 0l) {
						close();
					}
				}
			});
		}else {
			close();
		}
	}

	private void acceptQuery(ChannelHandlerContext ctx, String type) {
		try {
			if(pipelineData.listenerInfo.isAllowQuery()) {
				accepted = type;
				
			}
		}finally {
			if(maxAge <= 0l) {
				close();
			}
		}
	}

	@Override
	public <T> T get(IAttributeKey<T> key) {
		return pipelineData.get(key);
	}

	@Override
	public <T> void set(IAttributeKey<T> key, T value) {
		pipelineData.set(key, value);
	}

	@Override
	public boolean isClosed() {
		return !pipelineData.channel.isActive();
	}

	@Override
	public void close() {
		if(!dead) {
			dead = true;
			if(waitingPromiseCount.get() <= 0) {
				pipelineData.channel.close();
			}
		}
	}

	@Override
	public SocketAddress getRemoteAddress() {
		return pipelineData.channel.remoteAddress();
	}

	@Override
	public String getRealAddress() {
		return pipelineData.realAddress;
	}

	@Override
	public IEaglerListenerInfo getListenerInfo() {
		return pipelineData.listenerInfo;
	}

	@Override
	public String getAccept() {
		return accepted;
	}

	@Override
	public String getHeader(EnumWebSocketHeader header) {
		return pipelineData.getWebSocketHeader(header);
	}

	@Override
	public void setStringHandler(Consumer<String> handler) {
		stringHandler = handler;
	}

	@Override
	public void setBinaryHandler(Consumer<byte[]> handler) {
		binaryHandler = handler;
	}

	@Override
	public long getAge() {
		return Util.steadyTime() - createdAt;
	}

	@Override
	public void setMaxAge(long millis) {
		maxAge = millis;
	}

	@Override
	public long getMaxAge() {
		return maxAge;
	}

	@Override
	public void send(String string) {
		if(!dead) {
			waitingPromiseCount.incrementAndGet();
			pipelineData.channel.writeAndFlush(new TextWebSocketFrame(string)).addListener(writeListener);
		}
	}

	@Override
	public void send(byte[] bytes) {
		if(!dead) {
			waitingPromiseCount.incrementAndGet();
			pipelineData.channel.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(bytes))).addListener(writeListener);
		}
	}

	@Override
	public void sendResponse(String type, String str) {
		
	}

	@Override
	public void sendResponse(String type, JsonObject jsonObject) {
		
	}

	private void checkClose() {
		if(waitingPromiseCount.decrementAndGet() <= 0) {
			if(dead) {
				pipelineData.channel.close();
			}
		}
	}

}
