package net.lax1dude.eaglercraft.backend.server.api.webserver;

import java.net.SocketAddress;
import java.nio.charset.Charset;

public interface IRequestContext {

	IWebServer getServer();

	EnumRequestMethod getMethod();

	SocketAddress getSocketAddress();

	String getRealAddress();

	String getHost();

	String getPath();

	String getQuery();

	String getHeader(String name);

	void setResponseBody(IPreparedResponse preparedResponse);

	void setResponseBody(byte[] response);

	void setResponseBody(String response, Charset binaryCharset);

	void setResponseBodyEmpty();

	void setResponseHeader(String name, String value);

	void setResponseCode(int code);

	NettyUnsafe getNettyUnsafe();

	public interface NettyUnsafe {

		void setResponseBodyByteBuf(Object byteBuf);

		void setRequestHandled();

		Object getNettyChannelHandlerContext();

		Object getNettyHttpRequest();

	}

}
