package net.lax1dude.eaglercraft.backend.server.api.webserver;

import java.net.SocketAddress;
import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import net.lax1dude.eaglercraft.backend.server.api.EnumRequestMethod;

public interface IRequestContext {

	IWebServer getServer();

	EnumRequestMethod getMethod();

	SocketAddress getSocketAddress();

	String getRealAddress();

	String getHost();

	CharSequence getPath();

	CharSequence getQuery();

	CharSequence getHeader(String name);

	void setResponseBody(IPreparedResponse preparedResponse);

	void setResponseBody(byte[] response);

	void setResponseBody(CharSequence response, Charset binaryCharset);

	void setResponseBodyEmpty();

	void setResponseHeader(String name, Object value);

	void setResponseCode(int code);

	NettyUnsafe netty();

	public interface NettyUnsafe {

		void setResponseBodyByteBuf(ByteBuf byteBuf);

		void setResponseBodyHttpResponse(FullHttpResponse response);

		void setResponseSent();

		Channel getChannel();

		ChannelHandlerContext getChannelHandlerContext();

		FullHttpRequest getHttpRequest();

	}

}
