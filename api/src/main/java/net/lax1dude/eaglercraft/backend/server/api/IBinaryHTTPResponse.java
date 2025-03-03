package net.lax1dude.eaglercraft.backend.server.api;

import io.netty.buffer.ByteBuf;

public interface IBinaryHTTPResponse {

	boolean isSuccess();

	Throwable getFailureReason();

	boolean isRedirected();

	int getResponseCode();

	byte[] getResponseBody();

	NettyUnsafe getNettyUnsafe();

	public interface NettyUnsafe {

		ByteBuf getResponseBodyBuffer();

	}

}
