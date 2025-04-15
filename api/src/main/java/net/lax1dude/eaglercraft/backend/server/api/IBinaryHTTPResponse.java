package net.lax1dude.eaglercraft.backend.server.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;

public interface IBinaryHTTPResponse {

	boolean isSuccess();

	@Nullable
	Throwable getFailureReason();

	boolean isRedirected();

	int getResponseCode();

	@Nullable
	byte[] getResponseBody();

	@Nonnull
	NettyUnsafe netty();

	public interface NettyUnsafe {

		@Nullable
		ByteBuf getResponseBodyBuffer();

	}

}
