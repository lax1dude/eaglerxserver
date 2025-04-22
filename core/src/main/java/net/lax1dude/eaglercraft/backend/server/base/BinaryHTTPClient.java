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

package net.lax1dude.eaglercraft.backend.server.base;

import java.net.URI;
import java.util.function.Consumer;

import io.netty.buffer.ByteBuf;
import net.lax1dude.eaglercraft.backend.server.api.EnumRequestMethod;
import net.lax1dude.eaglercraft.backend.server.api.IBinaryHTTPClient;
import net.lax1dude.eaglercraft.backend.server.api.IBinaryHTTPResponse;
import net.lax1dude.eaglercraft.backend.skin_cache.IHTTPClient;
import net.lax1dude.eaglercraft.backend.skin_cache.IHTTPClient.Response;

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
			return response.redirected;
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
		public NettyUnsafe netty() {
			return this;
		}

		@Override
		public ByteBuf getResponseBodyBuffer() {
			ByteBuf buf = response.data;
			return buf != null ? buf.retainedSlice() : null;
		}

	}

	private final IHTTPClient httpClient;

	public BinaryHTTPClient(IHTTPClient httpClient) {
		this.httpClient = httpClient;
	}

	@Override
	public void asyncRequest(EnumRequestMethod method, URI requestURI, Consumer<IBinaryHTTPResponse> responseCallback) {
		if(method == null) {
			throw new NullPointerException("method");
		}
		if(requestURI == null) {
			throw new NullPointerException("requestURI");
		}
		if(responseCallback == null) {
			throw new NullPointerException("responseCallback");
		}
		httpClient.asyncRequest(method.name(), requestURI, new ResponseWrapper(responseCallback));
	}

}
