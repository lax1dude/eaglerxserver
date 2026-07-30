/*
 * Copyright (c) 2026 lax1dude. All Rights Reserved.
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

package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.compression.JZlibDecoder;
import io.netty.handler.codec.compression.JdkZlibDecoder;
import io.netty.handler.codec.compression.ZlibDecoder;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionEncoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtension;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtensionHandshaker;
import io.netty.handler.codec.http.websocketx.extensions.compression.DeflateFrameServerExtensionHandshaker;
import io.netty.handler.codec.http.websocketx.extensions.compression.PerMessageDeflateServerExtensionHandshaker;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;

public class WebSocketExtensionWrappers {

	private static final boolean hasMaxAllocationHandshaker;

	static {
		boolean hasMaxAlloc = false;
		try {
			PerMessageDeflateServerExtensionHandshaker.class.getConstructor(int.class, int.class);
			hasMaxAlloc = true;
		} catch (ReflectiveOperationException ex) {
		}
		hasMaxAllocationHandshaker = hasMaxAlloc;
	}

	public static WebSocketServerExtensionHandshaker createDeflateFrameServerExtensionHandshaker(int compressionLevel,
			int maxAllocation) {
		if (hasMaxAllocationHandshaker) {
			return new DeflateFrameServerExtensionHandshaker(compressionLevel, maxAllocation);
		} else {
			return new WebSocketServerExtensionHandshakerWrapper(
					new DeflateFrameServerExtensionHandshaker(compressionLevel), maxAllocation);
		}
	}

	public static WebSocketServerExtensionHandshaker createPerMessageDeflateServerExtensionHandshaker(
			int compressionLevel, boolean allowServerWindowSize, int preferredClientWindowSize,
			boolean allowServerNoContext, boolean preferredClientNoContext, int maxAllocation) {
		if (hasMaxAllocationHandshaker) {
			return new PerMessageDeflateServerExtensionHandshaker(compressionLevel, allowServerWindowSize,
					preferredClientWindowSize, allowServerNoContext, preferredClientNoContext, maxAllocation);
		} else {
			return new WebSocketServerExtensionHandshakerWrapper(
					new PerMessageDeflateServerExtensionHandshaker(compressionLevel, allowServerWindowSize,
							preferredClientWindowSize, allowServerNoContext, preferredClientNoContext),
					maxAllocation);
		}
	}

	public static class WebSocketServerExtensionHandshakerWrapper implements WebSocketServerExtensionHandshaker {

		private final WebSocketServerExtensionHandshaker delegate;
		private final int maxAllocation;

		public WebSocketServerExtensionHandshakerWrapper(WebSocketServerExtensionHandshaker delegate,
				int maxAllocation) {
			this.delegate = delegate;
			this.maxAllocation = maxAllocation;
		}

		@Override
		public WebSocketServerExtension handshakeExtension(WebSocketExtensionData extensionData) {
			WebSocketServerExtension ext = delegate.handshakeExtension(extensionData);
			return ext != null ? new WebSocketServerExtensionWrapper(ext, maxAllocation) : null;
		}

	}

	public static class WebSocketServerExtensionWrapper implements WebSocketServerExtension {

		private final WebSocketServerExtension delegate;
		private final int maxAllocation;

		public WebSocketServerExtensionWrapper(WebSocketServerExtension delegate, int maxAllocation) {
			this.delegate = delegate;
			this.maxAllocation = maxAllocation;
		}

		@Override
		public int rsv() {
			return delegate.rsv();
		}

		@Override
		public WebSocketExtensionEncoder newExtensionEncoder() {
			return delegate.newExtensionEncoder();
		}

		@Override
		public WebSocketExtensionDecoder newExtensionDecoder() {
			return new WebSocketExtensionDecoderWrapper(delegate.newExtensionDecoder(), maxAllocation);
		}

		@Override
		public WebSocketExtensionData newReponseData() {
			return delegate.newReponseData();
		}

	}

	public static class WebSocketExtensionDecoderWrapper extends WebSocketExtensionDecoder {

		private static final MethodHandle METH_DECODE;
		private static final VarHandle FIELD_DEFLATER;
		private static final boolean hasMaxAllocation;
		private static final boolean noJdkZlibDecoder;

		static {
			try {
				MethodHandles.Lookup lookup = MethodHandles.lookup();
				METH_DECODE = MethodHandles.privateLookupIn(MessageToMessageDecoder.class, lookup).findVirtual(
						MessageToMessageDecoder.class, "decode",
						MethodType.methodType(void.class, ChannelHandlerContext.class, Object.class, List.class));
				Class<?> clz = Class
						.forName("io.netty.handler.codec.http.websocketx.extensions.compression.DeflateDecoder");
				FIELD_DEFLATER = MethodHandles.privateLookupIn(clz, lookup).findVarHandle(clz, "decoder",
						EmbeddedChannel.class);
				boolean maxAlloc = false;
				try {
					ZlibDecoder.class.getConstructor(int.class);
					maxAlloc = true;
				} catch (ReflectiveOperationException ex) {
				}
				hasMaxAllocation = maxAlloc;
				noJdkZlibDecoder = PlatformDependent.javaVersion() < 7
						|| SystemPropertyUtil.getBoolean("io.netty.noJdkZlibDecoder", false);
				if (!hasMaxAllocation && noJdkZlibDecoder) {
					throw new IllegalStateException("Your Netty version is too old to use the JZlib decoder!");
				}
			} catch (ReflectiveOperationException ex) {
				throw new ExceptionInInitializerError(ex);
			}
		}

		private final WebSocketExtensionDecoder delegate;
		private final int maxAllocation;

		public WebSocketExtensionDecoderWrapper(WebSocketExtensionDecoder delegate, int maxAllocation) {
			this.delegate = delegate;
			this.maxAllocation = maxAllocation;
		}

		@Override
		protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
			if (FIELD_DEFLATER.get(delegate) == null) {
				if (!(msg instanceof TextWebSocketFrame) && !(msg instanceof BinaryWebSocketFrame)) {
					throw new CodecException("unexpected initial frame type: " + msg.getClass().getName());
				}
				FIELD_DEFLATER.set(delegate, new EmbeddedChannel(newWrappedZlibDecoder()));
			}
			try {
				METH_DECODE.invoke(delegate, ctx, msg, out);
			} catch (Exception | Error ex) {
				throw ex;
			} catch (Throwable exx) {
				throw new Error(exx);
			}
		}

		@Override
		public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
			delegate.handlerRemoved(ctx);
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			delegate.channelInactive(ctx);
		}

		@Override
		public boolean acceptInboundMessage(Object msg) throws Exception {
			return delegate.acceptInboundMessage(msg);
		}

		private ZlibDecoder newWrappedZlibDecoder() {
			if (hasMaxAllocation) {
				if (noJdkZlibDecoder) {
					return new JZlibDecoder(ZlibWrapper.NONE, maxAllocation);
				} else {
					return new JdkZlibDecoder(ZlibWrapper.NONE, true, maxAllocation);
				}
			} else {
				return new LegacyJdkZlibDecoder(ZlibWrapper.NONE, maxAllocation);
			}
		}

	}

}
