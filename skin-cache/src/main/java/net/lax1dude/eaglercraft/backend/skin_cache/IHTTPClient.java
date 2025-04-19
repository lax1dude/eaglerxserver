package net.lax1dude.eaglercraft.backend.skin_cache;

import java.net.URI;
import java.util.function.Consumer;

import io.netty.buffer.ByteBuf;

public interface IHTTPClient {

	public static class Response {

		public final int code;
		public final boolean redirected;
		public final ByteBuf data;
		public final Throwable exception;

		public Response(int code, boolean redirected, ByteBuf data) {
			this.code = code;
			this.redirected = redirected;
			this.data = data;
			this.exception = null;
		}

		public Response(Throwable exception) {
			this.code = -1;
			this.redirected = false;
			this.data = null;
			this.exception = exception;
		}

	}

	void asyncRequest(String method, URI uri, Consumer<Response> responseCallback);

}
