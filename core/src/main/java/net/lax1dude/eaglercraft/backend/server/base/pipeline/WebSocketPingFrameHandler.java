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

package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class WebSocketPingFrameHandler extends ChannelInboundHandlerAdapter {

	private long nextPing = 0l;
	private int pingQuota = 3;

	protected final IdleStateHandler readHandlerToNotify;
	protected final IdleStateHandler writeHandlerToNotify;
	protected final long eaglerPingTimeout;

	public WebSocketPingFrameHandler(IdleStateHandler readHandlerToNotify) {
		if (readHandlerToNotify != null) {
			long maxRead = Math.max(readHandlerToNotify.getReaderIdleTimeInMillis(),
					readHandlerToNotify.getAllIdleTimeInMillis());
			long maxWrite = Math.max(readHandlerToNotify.getWriterIdleTimeInMillis(),
					readHandlerToNotify.getAllIdleTimeInMillis());
			this.readHandlerToNotify = maxRead > 0l ? readHandlerToNotify : null;
			this.writeHandlerToNotify = maxWrite > 0l ? readHandlerToNotify : null;
			this.eaglerPingTimeout = maxRead > 0l ? Math.max(maxRead / 2l, maxRead - 10000l) * 1000000l : 0l;
		} else {
			this.readHandlerToNotify = null;
			this.writeHandlerToNotify = null;
			this.eaglerPingTimeout = 0l;
		}
	}

	protected static abstract class Hack implements GenericFutureListener<Future<Void>>, Runnable {

		private final EventExecutor eventLoop;

		protected Hack(EventExecutor eventLoop) {
			this.eventLoop = eventLoop;
		}

		@Override
		public void operationComplete(Future<Void> future) throws Exception {
			run();
		}

		@Override
		public void run() {
			if (eventLoop.inEventLoop()) {
				run0();
			} else {
				eventLoop.execute(this);
			}
		}

		protected abstract void run0();

	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof PingWebSocketFrame msg2) {
			msg2.release();
			long now = System.nanoTime();
			if (now > nextPing) {
				pingQuota = 3;
				nextPing = now + 3000000000l;
			}
			if (pingQuota > 0) {
				--pingQuota;
				if (readHandlerToNotify != null) {
					readHandlerToNotify.resetReadTimeout();
				}
				if (writeHandlerToNotify != null) {
					ctx.writeAndFlush(new PongWebSocketFrame()).addListener(new Hack(ctx.executor()) {
						@Override
						public void run0() {
							writeHandlerToNotify.resetWriteTimeout();
						}
					});
				} else {
					ctx.writeAndFlush(new PongWebSocketFrame(), ctx.voidPromise());
				}
			}
		} else if (!(msg instanceof PongWebSocketFrame msg2)) {
			ctx.fireChannelRead(msg);
		} else {
			msg2.release();
			if (readHandlerToNotify != null) {
				readHandlerToNotify.resetReadTimeout();
			}
		}
	}

}
