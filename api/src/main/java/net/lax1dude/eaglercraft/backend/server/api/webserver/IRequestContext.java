package net.lax1dude.eaglercraft.backend.server.api.webserver;

import java.net.SocketAddress;
import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import net.lax1dude.eaglercraft.backend.server.api.EnumRequestMethod;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;

public interface IRequestContext {

	IWebServer getServer();

	IEaglerListenerInfo getListener();

	EnumRequestMethod getMethod();

	SocketAddress getSocketAddress();

	String getRealAddress();

	String getRawPath();

	String getPath();

	String getQuery();

	String getHeader(String name);

	Iterable<String> getHeaders(String name);

	String getHost();

	String getOrigin();

	int getRequestBodyLength();

	default boolean hasRequestBody() {
		return getRequestBodyLength() > 0;
	}

	byte[] getRequestBodyByteArray();

	CharSequence getRequestBodyCharSequence(Charset charset);

	default String getRequestBodyString(Charset charset) {
		return getRequestBodyCharSequence(charset).toString();
	}

	default void getRequestBodyByteArray(byte[] dest) {
		getRequestBodyByteArray(0, dest, 0, dest.length);
	}

	void getRequestBodyByteArray(int srcOffset, byte[] dest, int dstOffset, int length);

	void setResponseBody(IPreparedResponse preparedResponse);

	void setResponseBody(byte[] response);

	void setResponseBody(CharSequence response, Charset binaryCharset);

	void setResponseBodyEmpty();

	void addResponseHeader(String name, Object value);

	void addResponseHeaders(String name, Iterable<?> value);

	void setResponseCode(int code);

	IContextPromise suspendContext();

	NettyUnsafe netty();

	public interface NettyUnsafe {

		Channel getChannel();

		ChannelHandlerContext getChannelHandlerContext();

		FullHttpRequest getHttpRequest();

		ByteBuf getRequestBodyByteBuf();

		void setResponseBodyByteBuf(ByteBuf byteBuf);

		void setResponseBodyHttpResponse(FullHttpResponse response);

		void setResponseSent();

	}

	public interface IContextPromise {

		IRequestContext context();

		void complete();

		void complete(Throwable err);

	}

}
