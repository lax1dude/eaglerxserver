package net.lax1dude.eaglercraft.backend.server.base.webserver;

import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import net.lax1dude.eaglercraft.backend.server.api.EnumRequestMethod;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IPreparedResponse;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IRequestContext;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IWebServer;

public class RequestContext implements IRequestContext, IRequestContext.NettyUnsafe {

	private final WebServer webServer;
	public EnumRequestMethod meth;
	public String uri;
	public String path;
	public String query;
	public ChannelHandlerContext ctx;
	public FullHttpRequest request;

	private static final int RESPONSE_NONE = 0;
	private static final int RESPONSE_PREPARED = 1;
	private static final int RESPONSE_BYTE_ARRAY = 2;
	private static final int RESPONSE_CHARS = 3;
	private static final int RESPONSE_EMPTY = 4;
	private static final int RESPONSE_UNSAFE_BUF = 5;
	private static final int RESPONSE_UNSAFE_FULL = 6;
	private static final int RESPONSE_UNSAFE_SENT = 7;

	public int responseCode = -1;
	public List<Object> responseHeaders = null;

	public int response = RESPONSE_NONE;
	public PreparedResponse responsePrepared;
	public byte[] responseData;
	public CharSequence responseChars;
	public Charset responseCharsCharset;
	public ByteBuf responseUnsafeByteBuf;
	public FullHttpResponse responseUnsafeFull;

	public RequestContext(WebServer webServer) {
		this.webServer = webServer;
	}

	public void setContext(EnumRequestMethod meth, String uri, String path, String query, ChannelHandlerContext ctx,
			FullHttpRequest request) {
		this.meth = meth;
		this.uri = uri;
		this.path = path;
		this.query = query;
		this.ctx = ctx;
		this.request = request;
		clearResult();
		this.response = RESPONSE_NONE;
		this.responseCode = -1;
		this.responseHeaders = null;
	}

	public void clearResult() {
		if(response != RESPONSE_NONE) {
			switch(response) {
			case RESPONSE_PREPARED:
				if(responsePrepared != null) {
					responsePrepared.release();
					responsePrepared = null;
				}
				break;
			case RESPONSE_BYTE_ARRAY:
				responseData = null;
				break;
			case RESPONSE_CHARS:
				responseChars = null;
				responseCharsCharset = null;
				break;
			case RESPONSE_UNSAFE_BUF:
				if(responseUnsafeByteBuf != null) {
					responseUnsafeByteBuf.release();
					responseUnsafeByteBuf = null;
				}
				break;
			case RESPONSE_UNSAFE_FULL:
				if(responseUnsafeFull != null) {
					responseUnsafeFull.release();
					responseUnsafeFull = null;
				}
				break;
			}
		}
	}

	@Override
	public IWebServer getServer() {
		return webServer;
	}

	@Override
	public EnumRequestMethod getMethod() {
		return meth;
	}

	@Override
	public SocketAddress getSocketAddress() {
		return ctx.channel().remoteAddress();
	}

	@Override
	public String getRealAddress() {
		// TODO
		return null;
	}

	@Override
	public String getRawPath() {
		return uri;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public String getQuery() {
		return query;
	}

	@Override
	public String getHeader(String name) {
		return request.headers().get(name);
	}

	@Override
	public String getHost() {
		return request.headers().get(HttpHeaderNames.HOST);
	}

	@Override
	public void setResponseBody(IPreparedResponse preparedResponse) {
		clearResult();
		this.response = RESPONSE_PREPARED;
		this.responsePrepared = (PreparedResponse) preparedResponse;
	}

	@Override
	public void setResponseBody(byte[] response) {
		clearResult();
		this.response = RESPONSE_BYTE_ARRAY;
		this.responseData = response;
	}

	@Override
	public void setResponseBody(CharSequence response, Charset binaryCharset) {
		clearResult();
		this.response = RESPONSE_CHARS;
		this.responseChars = response;
		this.responseCharsCharset = binaryCharset;
	}

	@Override
	public void setResponseBodyEmpty() {
		clearResult();
		this.response = RESPONSE_EMPTY;
	}

	@Override
	public void setResponseHeader(String name, Object value) {
		if(responseHeaders == null) {
			responseHeaders = new ArrayList<>();
		}
		responseHeaders.add(name);
		responseHeaders.add(value);
	}

	@Override
	public void setResponseCode(int code) {
		this.responseCode = code;
	}

	@Override
	public NettyUnsafe netty() {
		return this;
	}

	@Override
	public void setResponseBodyByteBuf(ByteBuf byteBuf) {
		clearResult();
		this.response = RESPONSE_UNSAFE_BUF;
		this.responseUnsafeByteBuf = byteBuf;
	}

	@Override
	public void setResponseBodyHttpResponse(FullHttpResponse response) {
		clearResult();
		this.response = RESPONSE_UNSAFE_FULL;
		this.responseUnsafeFull = response;
	}

	@Override
	public void setResponseSent() {
		clearResult();
		this.response = RESPONSE_UNSAFE_SENT;
	}

	@Override
	public Channel getChannel() {
		return ctx.channel();
	}

	@Override
	public ChannelHandlerContext getChannelHandlerContext() {
		return ctx;
	}

	@Override
	public FullHttpRequest getHttpRequest() {
		return request;
	}

}
