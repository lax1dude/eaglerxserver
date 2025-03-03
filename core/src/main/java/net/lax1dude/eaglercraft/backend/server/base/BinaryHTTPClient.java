package net.lax1dude.eaglercraft.backend.server.base;

import java.net.URI;
import java.util.function.Consumer;

import io.netty.buffer.ByteBuf;
import net.lax1dude.eaglercraft.backend.server.api.EnumRequestMethod;
import net.lax1dude.eaglercraft.backend.server.api.IBinaryHTTPClient;
import net.lax1dude.eaglercraft.backend.server.api.IBinaryHTTPResponse;
import net.lax1dude.eaglercraft.backend.skin_cache.HTTPClient;
import net.lax1dude.eaglercraft.backend.skin_cache.HTTPClient.Response;

public class BinaryHTTPClient implements IBinaryHTTPClient {

	private static class ResponseWrapper implements Consumer<Response>, IBinaryHTTPResponse, IBinaryHTTPResponse.NettyUnsafe {

		private final Consumer<IBinaryHTTPResponse> responseCallback;
		private Response response;

		public ResponseWrapper(Consumer<IBinaryHTTPResponse> responseCallback) {
			this.responseCallback = responseCallback;
		}

		@Override
		public void accept(Response t) {
			try {
				if(response != null || t == null) {
					throw new IllegalStateException();
				}
				response = t;
				responseCallback.accept(this);
			}finally {
				if(t.data != null) {
					t.data.release();
				}
			}
		}

		@Override
		public boolean isSuccess() {
			return response.exception == null;
		}

		@Override
		public Throwable getFailureReason() {
			return response.exception;
		}

		@Override
		public boolean isRedirected() {
			return false; //TODO
		}

		@Override
		public int getResponseCode() {
			return response.code;
		}

		@Override
		public byte[] getResponseBody() {
			ByteBuf buf = response.data;
			if(buf == null) {
				return null;
			}
			byte[] data = new byte[buf.readableBytes()];
			buf.getBytes(buf.readerIndex(), data);
			return data;
		}

		@Override
		public NettyUnsafe getNettyUnsafe() {
			return this;
		}

		@Override
		public ByteBuf getResponseBodyBuffer() {
			ByteBuf buf = response.data;
			return buf != null ? buf.retainedSlice() : null;
		}

	}

	private final HTTPClient httpClient;

	public BinaryHTTPClient(HTTPClient httpClient) {
		this.httpClient = httpClient;
	}

	@Override
	public void asyncRequest(EnumRequestMethod method, URI requestURI, Consumer<IBinaryHTTPResponse> responseCallback) {
		httpClient.asyncRequest(method.name(), requestURI, new ResponseWrapper(responseCallback));
	}

}
