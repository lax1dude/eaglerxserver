package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import java.net.SocketAddress;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.ReferenceCountUtil;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformTask;
import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeKey;
import net.lax1dude.eaglercraft.backend.server.api.query.IDuplexBaseHandler;
import net.lax1dude.eaglercraft.backend.server.api.query.IDuplexBinaryHandler;
import net.lax1dude.eaglercraft.backend.server.api.query.IDuplexJSONHandler;
import net.lax1dude.eaglercraft.backend.server.api.query.IDuplexStringHandler;
import net.lax1dude.eaglercraft.backend.server.api.query.IQueryConnection;
import net.lax1dude.eaglercraft.backend.server.api.query.IQueryHandler;
import net.lax1dude.eaglercraft.backend.server.base.EaglerListener;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.MOTDConnectionWrapper;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;
import net.lax1dude.eaglercraft.backend.server.util.Util;

public class WebSocketQueryHandler extends ChannelInboundHandlerAdapter implements IQueryConnection {

	private final EaglerXServer<?> server;
	private final NettyPipelineData pipelineData;
	private final long createdAt;

	private boolean initial = true;
	private boolean handled = false;
	private volatile boolean dead = false;
	private String accepted = null;
	private IDuplexStringHandler stringHandler = null;
	private IDuplexJSONHandler jsonHandler = null;
	private IDuplexBinaryHandler binaryHandler = null;
	private long maxAge = -1l;
	private IPlatformTask closeTask = null;

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
				dead = true;
				ctx.close();
			}else {
				if(!handled) {
					handled = true;
					if(msg instanceof TextWebSocketFrame) {
						String accept = ((TextWebSocketFrame)msg).text();
						if(accept.length() < 128) {
							accept = accept.toLowerCase(Locale.US);
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
					dead = true;
					ctx.close();
				}else {
					if(msg instanceof TextWebSocketFrame) {
						String txt = ((TextWebSocketFrame)msg).text();
						if(jsonHandler != null) {
							JsonElement el = null;
							try {
								el = JsonParser.parseString(txt);
							}catch(JsonSyntaxException ex) {
							}
							if(el != null && el.isJsonObject()) {
								jsonHandler.handleJSONObject(this, el.getAsJsonObject());
								return;
							}
						}
						if(stringHandler != null) {
							stringHandler.handleString(this, txt);
						}else {
							dead = true;
							ctx.close();
						}
					}else if(msg instanceof BinaryWebSocketFrame) {
						if(binaryHandler != null) {
							ByteBuf buf = ((BinaryWebSocketFrame)msg).content();
							byte[] data = new byte[buf.readableBytes()];
							buf.readBytes(data);
							binaryHandler.handleBinary(this, data);
						}else {
							dead = true;
							ctx.close();
						}
					}
				}
			}
		}finally {
			ReferenceCountUtil.release(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (ctx.channel().isActive()) {
			pipelineData.connectionLogger.error("Uncaught exception in handler pipeline", cause);
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		dead = true;
	}

	private void acceptMOTD(ChannelHandlerContext ctx, String type) {
		if(pipelineData.listenerInfo.isAllowMOTD()) {
			accepted = type;
			MOTDConnectionWrapper motdConnection = new MOTDConnectionWrapper(this);
			motdConnection.setDefaults(server);
			server.eventDispatcher().dispatchMOTDEvent(motdConnection, (motdEvent, err) -> {
				try {
					if(err != null) {
						maxAge = -1l;
						pipelineData.connectionLogger.error("MOTD event handler raised an exception", err);
					}else {
						motdEvent.getMOTDConnection().sendToUser();
					}
				}finally {
					if(maxAge <= 0l) {
						close();
					}
					initial = false;
				}
			});
		}else {
			dead = true;
			ctx.close();
		}
	}

	private void acceptQuery(ChannelHandlerContext ctx, String type) {
		if(server.testServerListConfirmCode(type)) {
			dead = true;
			ctx.writeAndFlush(new TextWebSocketFrame("OK")).addListener(ChannelFutureListener.CLOSE);
			return;
		}
		if(pipelineData.listenerInfo.isAllowQuery()) {
			IQueryHandler handler = server.getQueryServer().getHandlerFor(type);
			if(handler != null) {
				try {
					accepted = type;
					handler.handleQuery(this);
					return;
				}finally {
					if(maxAge <= 0l) {
						close();
					}
					initial = false;
				}
			}
		}
		dead = true;
		ctx.close();
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
		return dead || !pipelineData.channel.isActive();
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
	public EaglerListener getListenerInfo() {
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
	public void setHandlers(IDuplexBaseHandler compositeHandler) {
		if(compositeHandler instanceof IDuplexStringHandler) {
			stringHandler = (IDuplexStringHandler) compositeHandler;
		}
		if(compositeHandler instanceof IDuplexJSONHandler) {
			jsonHandler = (IDuplexJSONHandler) compositeHandler;
		}
		if(compositeHandler instanceof IDuplexBinaryHandler) {
			binaryHandler = (IDuplexBinaryHandler) compositeHandler;
		}
	}

	@Override
	public void setHandlers(IDuplexBaseHandler... compositeHandlers) {
		for(int i = 0; i < compositeHandlers.length; ++i) {
			setHandlers(compositeHandlers[i]);
		}
	}

	@Override
	public void setStringHandler(IDuplexStringHandler handler) {
		stringHandler = handler;
	}

	@Override
	public void setJSONHandler(IDuplexJSONHandler handler) {
		jsonHandler = handler;
	}

	@Override
	public void setBinaryHandler(IDuplexBinaryHandler handler) {
		binaryHandler = handler;
	}

	@Override
	public long getAge() {
		return Util.steadyTime() - createdAt;
	}

	@Override
	public void setMaxAge(long millis) {
		if(!dead) {
			if(maxAge != millis) {
				maxAge = millis;
				if(closeTask != null) {
					closeTask.cancel();
				}
				if(millis > 0l) {
					long closeAfter = maxAge - getAge();
					if(closeAfter > 0l) {
						closeTask = server.getPlatform().getScheduler().executeAsyncDelayedTask(this::close, closeAfter);
					}else {
						close();
					}
				}else {
					if(!initial) {
						close();
					}
				}
			}
		}
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
		if(!dead) {
			send(server.getQueryServer().createStringResponse(type, str).toString());
		}
	}

	@Override
	public void sendResponse(String type, JsonObject jsonObject) {
		if(!dead) {
			send(server.getQueryServer().createJsonObjectResponse(type, jsonObject).toString());
		}
	}

	private void checkClose() {
		if(waitingPromiseCount.decrementAndGet() <= 0) {
			if(dead) {
				pipelineData.channel.close();
			}
		}
	}

}
