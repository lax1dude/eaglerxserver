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

package net.lax1dude.eaglercraft.backend.server.api.webserver;

import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import net.lax1dude.eaglercraft.backend.server.api.EnumRequestMethod;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;

public interface IRequestContext {

	@Nonnull
	IWebServer getServer();

	@Nonnull
	IEaglerListenerInfo getListener();

	@Nonnull
	EnumRequestMethod getMethod();

	@Nonnull
	SocketAddress getSocketAddress();

	@Nullable
	String getRealAddress();

	@Nonnull
	String getRawPath();

	@Nonnull
	String getPath();

	@Nonnull
	String getQuery();

	@Nullable
	String getHeader(@Nonnull String name);

	@Nonnull
	List<String> getHeaders(@Nonnull String name);

	@Nullable
	String getHost();

	@Nullable
	String getOrigin();

	int getRequestBodyLength();

	default boolean hasRequestBody() {
		return getRequestBodyLength() > 0;
	}

	@Nonnull
	byte[] getRequestBodyByteArray();

	@Nonnull
	CharSequence getRequestBodyCharSequence(@Nonnull Charset charset);

	@Nonnull
	default String getRequestBodyString(@Nonnull Charset charset) {
		return getRequestBodyCharSequence(charset).toString();
	}

	default void getRequestBodyByteArray(@Nonnull byte[] dest) {
		getRequestBodyByteArray(0, dest, 0, dest.length);
	}

	void getRequestBodyByteArray(int srcOffset, @Nonnull byte[] dest, int dstOffset, int length);

	void setResponseBody(@Nonnull IPreparedResponse preparedResponse);

	void setResponseBody(@Nonnull byte[] response);

	void setResponseBody(@Nonnull CharSequence response, @Nonnull Charset binaryCharset);

	void setResponseBodyEmpty();

	void addResponseHeader(@Nonnull String name, @Nonnull Object value);

	void addResponseHeaders(@Nonnull String name, @Nonnull Iterable<?> value);

	void setResponseCode(int code);

	@Nonnull
	IContextPromise suspendContext();

	@Nonnull
	NettyUnsafe netty();

	public interface NettyUnsafe {

		@Nonnull
		Channel getChannel();

		@Nonnull
		ChannelHandlerContext getChannelHandlerContext();

		@Nonnull
		FullHttpRequest getHttpRequest();

		@Nonnull
		ByteBuf getRequestBodyByteBuf();

		void setResponseBodyByteBuf(@Nonnull ByteBuf byteBuf);

		void setResponseBodyHttpResponse(@Nonnull FullHttpResponse response);

	}

	public interface IContextPromise {

		@Nonnull
		IRequestContext context();

		void complete();

		void complete(@Nonnull Throwable err);

	}

}
