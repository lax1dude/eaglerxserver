package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.net.SocketAddress;
import java.util.Locale;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.ReferenceCountUtil;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformTask;
import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.server.api.INettyChannel;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeKey;
import net.lax1dude.eaglercraft.backend.server.api.query.IDuplexBaseHandler;
import net.lax1dude.eaglercraft.backend.server.api.query.IDuplexBinaryHandler;
import net.lax1dude.eaglercraft.backend.server.api.query.IDuplexJSONHandler;
import net.lax1dude.eaglercraft.backend.server.api.query.IDuplexStringHandler;
import net.lax1dude.eaglercraft.backend.server.api.query.IQueryConnection;
import net.lax1dude.eaglercraft.backend.server.api.query.IQueryHandler;
import net.lax1dude.eaglercraft.backend.server.base.EaglerListener;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.IIdentifiedConnection;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;
import net.lax1dude.eaglercraft.backend.server.base.query.MOTDConnectionWrapper;
import net.lax1dude.eaglercraft.backend.server.util.Util;

public class WebSocketQueryHandler extends ChannelInboundHandlerAdapter
		implements IQueryConnection, IIdentifiedConnection, INettyChannel.NettyUnsafe {

	private static final VarHandle WAITING_PROMISE_HANDLE;
	private static final VarHandle DISCONNECT_CALLED_HANDLE;

	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			WAITING_PROMISE_HANDLE = l.findVarHandle(WebSocketQueryHandler.class, "waitingPromiseCount", int.class);
			DISCONNECT_CALLED_HANDLE = l.findVarHandle(WebSocketQueryHandler.class, "disconnectCalled", int.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private volatile int waitingPromiseCount = 1;
	private volatile int disconnectCalled = 0;

	private final EaglerXServer<?> server;
	private final NettyPipelineData pipelineData;
	private final long createdAt;

	private boolean initial = true;
	private boolean handled = false;
	private String accepted = null;
	private IDuplexStringHandler stringHandler = null;
	private IDuplexJSONHandler jsonHandler = null;
	private IDuplexBinaryHandler binaryHandler = null;
	private long maxAge = -1l;
	private IPlatformTask closeTask = null;

	private final ChannelFutureListener writeListener = (e) -> {
		WebSocketQueryHandler.this.checkClose();
	};

	public WebSocketQueryHandler(EaglerXServer<?> server, NettyPipelineData pipelineData) {
		this.server = server;
		this.pipelineData = pipelineData;
		this.createdAt = Util.steadyTime();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			if(msg instanceof CloseWebSocketFrame) {
				WAITING_PROMISE_HANDLE.setRelease(this, 0);
				ctx.close();
			}else {
				if(!handled) {
					handled = true;
					if(msg instanceof TextWebSocketFrame msg2) {
						String accept = msg2.text();
						if(accept.length() < 128) {
							accept = accept.toLowerCase(Locale.US);
							if(accept.startsWith("accept:")) {
								accept = accept.substring(7).trim();
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
					WAITING_PROMISE_HANDLE.setRelease(this, 0);
					ctx.close();
				}else {
					if(msg instanceof TextWebSocketFrame msg2) {
						String txt = msg2.text();
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
							WAITING_PROMISE_HANDLE.setRelease(this, 0);
							ctx.close();
						}
					}else if(msg instanceof BinaryWebSocketFrame msg2) {
						if(binaryHandler != null) {
							ByteBuf buf = msg2.content();
							byte[] data = new byte[buf.readableBytes()];
							buf.readBytes(data);
							binaryHandler.handleBinary(this, data);
						}else {
							WAITING_PROMISE_HANDLE.setRelease(this, 0);
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
		WAITING_PROMISE_HANDLE.setRelease(this, 0);
		super.channelInactive(ctx);
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
						disconnect();
					}
					initial = false;
				}
			});
		}else {
			WAITING_PROMISE_HANDLE.setRelease(this, 0);
			ctx.close();
		}
	}

	private void acceptQuery(ChannelHandlerContext ctx, String type) {
		if(server.testServerListConfirmCode(type)) {
			WAITING_PROMISE_HANDLE.setRelease(this, 0);
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
						disconnect();
					}
					initial = false;
				}
			}
		}
		WAITING_PROMISE_HANDLE.setRelease(this, 0);
		ctx.close();
	}

	@Override
	public Object getIdentityToken() {
		return pipelineData.attributeHolder;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(pipelineData.attributeHolder);
	}

	public boolean equals(Object o) {
		return this == o || ((o instanceof IIdentifiedConnection oo) && oo.getIdentityToken() == pipelineData.attributeHolder);
	}

	@Override
	public <T> T get(IAttributeKey<T> key) {
		return pipelineData.attributeHolder.get(key);
	}

	@Override
	public <T> void set(IAttributeKey<T> key, T value) {
		pipelineData.attributeHolder.set(key, value);
	}

	@Override
	public boolean isConnected() {
		return (int)WAITING_PROMISE_HANDLE.getAcquire(this) > 0 && pipelineData.channel.isActive();
	}

	@Override
	public void disconnect() {
		Thread.dumpStack();
		if((int)DISCONNECT_CALLED_HANDLE.getAndSetAcquire(this, 1) == 0) {
			checkClose();
		}
	}

	@Override
	public SocketAddress getSocketAddress() {
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
	public boolean isWebSocketSecure() {
		return pipelineData.wss;
	}

	@Override
	public String getWebSocketHeader(EnumWebSocketHeader header) {
		return pipelineData.getWebSocketHeader(header);
	}

	@Override
	public String getWebSocketPath() {
		return pipelineData.getWebSocketPath();
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
		if((int)WAITING_PROMISE_HANDLE.getAcquire(this) > 0) {
			if(maxAge != millis) {
				maxAge = millis;
				if(closeTask != null) {
					closeTask.cancel();
				}
				if(millis > 0l) {
					long closeAfter = maxAge - getAge();
					if(closeAfter > 0l) {
						closeTask = server.getPlatform().getScheduler().executeAsyncDelayedTask(this::disconnect, closeAfter);
					}else {
						disconnect();
					}
				}else {
					if(!initial) {
						disconnect();
					}
				}
			}
		}
	}

	@Override
	public long getMaxAge() {
		return maxAge;
	}

	private boolean aquireSend() {
		int i;
		for(;;) {
			i = (int)WAITING_PROMISE_HANDLE.getAcquire(this);
			if(i > 0) {
				if((int)WAITING_PROMISE_HANDLE.compareAndExchangeAcquire(this, i, i + 1) == i) {
					return true;
				}
			}else {
				return false;
			}
		}
	}

	@Override
	public void send(String string) {
		if(aquireSend()) {
			pipelineData.channel.eventLoop().execute(() -> pipelineData.channel
					.writeAndFlush(new TextWebSocketFrame(string)).addListener(writeListener));
		}
	}

	@Override
	public void send(byte[] bytes) {
		if(aquireSend()) {
			pipelineData.channel.eventLoop().execute(() -> pipelineData.channel
					.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(bytes))).addListener(writeListener));
		}
	}

	@Override
	public void sendResponse(String type, String str) {
		if(aquireSend()) {
			pipelineData.channel.eventLoop().execute(() -> pipelineData.channel
					.writeAndFlush(new TextWebSocketFrame(server.getQueryServer().createStringResponse(type, str).toString()))
					.addListener(writeListener));
		}
	}

	@Override
	public void sendResponse(String type, JsonObject jsonObject) {
		if(aquireSend()) {
			pipelineData.channel.eventLoop().execute(() -> pipelineData.channel
					.writeAndFlush(new TextWebSocketFrame(server.getQueryServer().createJsonObjectResponse(type, jsonObject).toString()))
					.addListener(writeListener));
		}
	}

	@Override
	public NettyUnsafe netty() {
		return this;
	}

	@Override
	public Channel getChannel() {
		return pipelineData.channel;
	}

	private void checkClose() {
		if((int)WAITING_PROMISE_HANDLE.getAndAddRelease(this, -1) == 1) {
			pipelineData.channel.close();
		}
	}

}
