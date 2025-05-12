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

package net.lax1dude.eaglercraft.backend.server.base.webserver;

import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import net.lax1dude.eaglercraft.backend.server.adapter.event.IEventDispatchCallback;
import net.lax1dude.eaglercraft.backend.server.api.EnumRequestMethod;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IPreflightContext;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IPreparedResponse;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IRequestContext;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IRequestHandler;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IWebServer;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.HTTPMessageUtils;

public class RequestContext implements IPreflightContext, IRequestContext.NettyUnsafe {

	private final WebServer webServer;
	public IEaglerListenerInfo listener;
	public EnumRequestMethod meth;
	public EnumRequestMethod pfMeth;
	public String uri;
	public String path;
	public String query;
	public ChannelHandlerContext ctx;
	public FullHttpRequest request;
	public String realAddress;
	public IRequestHandler requestHandlerInternal;
	public boolean failing;

	public static final int RESPONSE_NONE = 0;
	public static final int RESPONSE_PREPARED = 1;
	public static final int RESPONSE_BYTE_ARRAY = 2;
	public static final int RESPONSE_CHARS = 3;
	public static final int RESPONSE_EMPTY = 4;
	public static final int RESPONSE_UNSAFE_BUF = 5;
	public static final int RESPONSE_UNSAFE_FULL = 6;

	public int responseCode = -1;
	public List<Object> responseHeaders = null;

	public int response = RESPONSE_NONE;
	public PreparedResponse responsePrepared;
	public byte[] responseData;
	public CharSequence responseChars;
	public Charset responseCharsCharset;
	public ByteBuf responseUnsafeByteBuf;
	public FullHttpResponse responseUnsafeFull;

	public boolean suspendable = false;
	public ContextPromise contextPromise;
	public ResponseOrdering.Slot responseSlotTmp;

	public RequestContext(WebServer webServer) {
		this.webServer = webServer;
	}

	public void setContext(IEaglerListenerInfo listener, EnumRequestMethod meth, EnumRequestMethod pfMeth, String uri,
			String path, String query, ChannelHandlerContext ctx, FullHttpRequest request, String realAddress) {
		this.listener = listener;
		this.meth = meth;
		this.pfMeth = pfMeth;
		this.uri = uri;
		this.path = path;
		this.query = query;
		this.ctx = ctx;
		this.request = request;
		this.realAddress = realAddress;
		clearResult();
		this.response = RESPONSE_NONE;
		this.responseCode = -1;
		this.responseHeaders = null;
	}

	public void clearResult() {
		if (response != RESPONSE_NONE) {
			switch (response) {
			case RESPONSE_PREPARED:
				if (responsePrepared != null) {
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
				if (responseUnsafeByteBuf != null) {
					responseUnsafeByteBuf.release();
					responseUnsafeByteBuf = null;
				}
				break;
			case RESPONSE_UNSAFE_FULL:
				if (responseUnsafeFull != null) {
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
	public IEaglerListenerInfo getListener() {
		return listener;
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
		return realAddress;
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
		if (name == null) {
			throw new NullPointerException("name");
		}
		return request.headers().get(name);
	}

	@Override
	public List<String> getHeaders(String name) {
		if (name == null) {
			throw new NullPointerException("name");
		}
		return request.headers().getAll(name);
	}

	@Override
	public String getHost() {
		return request.headers().get("host");
	}

	@Override
	public String getOrigin() {
		return request.headers().get("origin");
	}

	@Override
	public int getRequestBodyLength() {
		return request.content().readableBytes();
	}

	@Override
	public byte[] getRequestBodyByteArray() {
		ByteBuf buf = request.content();
		int len = buf.readableBytes();
		if (len == 0) {
			throw new UnsupportedOperationException("Request does not have a body");
		}
		byte[] ret = new byte[len];
		buf.getBytes(buf.readerIndex(), ret);
		return ret;
	}

	@Override
	public CharSequence getRequestBodyCharSequence(Charset charset) {
		if (charset == null) {
			throw new NullPointerException("charset");
		}
		ByteBuf buf = request.content();
		int len = buf.readableBytes();
		if (len == 0) {
			throw new UnsupportedOperationException("Request does not have a body");
		}
		return buf.getCharSequence(buf.readerIndex(), len, charset);
	}

	@Override
	public void getRequestBodyByteArray(int srcOffset, byte[] dest, int dstOffset, int length) {
		ByteBuf buf = request.content();
		if (buf.readableBytes() == 0) {
			throw new UnsupportedOperationException("Request does not have a body");
		}
		buf.getBytes(buf.readerIndex() + srcOffset, dest, dstOffset, length);
	}

	@Override
	public void setResponseBody(IPreparedResponse preparedResponse) {
		if (preparedResponse == null) {
			throw new NullPointerException("response body is null");
		}
		clearResult();
		this.response = RESPONSE_PREPARED;
		this.responsePrepared = (PreparedResponse) preparedResponse;
	}

	@Override
	public void setResponseBody(byte[] response) {
		if (response == null) {
			throw new NullPointerException("response body is null");
		}
		clearResult();
		this.response = RESPONSE_BYTE_ARRAY;
		this.responseData = response;
	}

	@Override
	public void setResponseBody(CharSequence response, Charset binaryCharset) {
		if (response == null) {
			throw new NullPointerException("response body is null");
		}
		if (binaryCharset == null) {
			throw new NullPointerException("response charset is null");
		}
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
	public void addResponseHeader(String name, Object value) {
		if (name == null) {
			throw new NullPointerException("header name is null");
		}
		if (value == null) {
			throw new NullPointerException("header value is null");
		}
		if (responseHeaders == null) {
			responseHeaders = new ArrayList<>();
		}
		responseHeaders.add(name);
		responseHeaders.add(value);
	}

	@Override
	public void addResponseHeaders(String name, Iterable<?> values) {
		if (name == null) {
			throw new NullPointerException("header name is null");
		}
		if (values == null) {
			throw new NullPointerException("header values is null");
		}
		if (responseHeaders == null) {
			responseHeaders = new ArrayList<>();
		}
		for (Object val : values) {
			responseHeaders.add(name);
			responseHeaders.add(val);
		}
	}

	@Override
	public void setResponseCode(int code) {
		this.responseCode = code;
	}

	@Override
	public IContextPromise suspendContext() {
		if (!ctx.channel().eventLoop().inEventLoop()) {
			throw new IllegalStateException("Cannot suspend context outside of the channel's event loop");
		}
		if (!suspendable) {
			throw new IllegalStateException("Context was suspended after the request was already handled");
		}
		if (contextPromise != null) {
			throw new IllegalStateException("Context has already been suspended");
		}
		request.retain();
		return contextPromise = new ContextPromise();
	}

	@Override
	public EnumRequestMethod getRequestedMethod() {
		return pfMeth;
	}

	@Override
	public Iterable<String> getRequestedHeaders() {
		return HTTPMessageUtils.getAllValues(request.headers(), "access-control-request-headers");
	}

	@Override
	public NettyUnsafe netty() {
		return this;
	}

	@Override
	public void setResponseBodyByteBuf(ByteBuf byteBuf) {
		if (byteBuf == null) {
			throw new NullPointerException("response ByteBuf is null");
		}
		clearResult();
		this.response = RESPONSE_UNSAFE_BUF;
		this.responseUnsafeByteBuf = byteBuf;
	}

	@Override
	public void setResponseBodyHttpResponse(FullHttpResponse response) {
		if (response == null) {
			throw new NullPointerException("response FullHttpResponse is null");
		}
		clearResult();
		this.response = RESPONSE_UNSAFE_FULL;
		this.responseUnsafeFull = response;
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

	@Override
	public ByteBuf getRequestBodyByteBuf() {
		return request.content();
	}

	public class ContextPromise implements IContextPromise {

		private IEventDispatchCallback<RequestContext> consumer;
		private boolean complete;
		private Throwable error;

		@Override
		public void complete() {
			IEventDispatchCallback<RequestContext> cb;
			synchronized (this) {
				if (complete) {
					return;
				}
				complete = true;
				error = null;
				cb = consumer;
			}
			if (cb != null) {
				EventLoop el = ctx.channel().eventLoop();
				if (el.inEventLoop()) {
					cb.complete(RequestContext.this, null);
				} else {
					el.execute(() -> {
						cb.complete(RequestContext.this, null);
					});
				}
			}
		}

		@Override
		public void complete(Throwable err) {
			IEventDispatchCallback<RequestContext> cb;
			synchronized (this) {
				if (complete) {
					return;
				}
				complete = true;
				error = err;
				cb = consumer;
			}
			if (cb != null) {
				EventLoop el = ctx.channel().eventLoop();
				if (el.inEventLoop()) {
					cb.complete(RequestContext.this, err);
				} else {
					el.execute(() -> {
						cb.complete(RequestContext.this, err);
					});
				}
			}
		}

		@Override
		public IRequestContext context() {
			return RequestContext.this;
		}

		public void onResumeInternal(IEventDispatchCallback<RequestContext> cs) {
			Throwable err;
			synchronized (this) {
				if (!complete) {
					consumer = cs;
					return;
				}
				err = error;
			}
			cs.complete(RequestContext.this, err);
		}

	}

}
