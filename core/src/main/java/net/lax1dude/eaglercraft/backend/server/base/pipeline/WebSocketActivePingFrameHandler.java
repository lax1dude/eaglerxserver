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

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.ScheduledFuture;

public class WebSocketActivePingFrameHandler extends WebSocketPingFrameHandler implements Runnable {

	private long lastChannelRead;
	private ChannelHandlerContext ctx;
	private ScheduledFuture<?> eaglerPingTask;
	private boolean reading;

	public WebSocketActivePingFrameHandler(IdleStateHandler readHandlerToNotify) {
		super(readHandlerToNotify);
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		if (eaglerPingTimeout > 0l) {
			this.ctx = ctx;
			lastChannelRead = System.nanoTime();
			eaglerPingTask = ctx.executor().schedule(this, eaglerPingTimeout, TimeUnit.NANOSECONDS);
		}
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		if (eaglerPingTask != null) {
			this.ctx = null;
			eaglerPingTask.cancel(false);
			eaglerPingTask = null;
		}
	}

	@Override
	public void run() {
		ChannelHandlerContext ctx = this.ctx;
		if (ctx == null || !ctx.channel().isOpen()) {
			return;
		}
		long nextDelay = eaglerPingTimeout;
		if (!reading) {
			nextDelay -= System.nanoTime() - lastChannelRead;
		}
		if (nextDelay <= 0l) {
			eaglerPingTask = ctx.executor().schedule(this, eaglerPingTimeout, TimeUnit.NANOSECONDS);
			if (writeHandlerToNotify != null) {
				ctx.writeAndFlush(new PingWebSocketFrame()).addListener(new Hack(ctx.executor()) {
					@Override
					public void run0() {
						writeHandlerToNotify.resetWriteTimeout();
					}
				});
			} else {
				ctx.writeAndFlush(new PingWebSocketFrame(), ctx.voidPromise());
			}
		} else {
			eaglerPingTask = ctx.executor().schedule(this, nextDelay, TimeUnit.NANOSECONDS);
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (eaglerPingTimeout > 0l) {
			reading = true;
		}
		super.channelRead(ctx, msg);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		if (reading) {
			lastChannelRead = System.nanoTime();
            reading = false;
        }
		ctx.fireChannelReadComplete();
	}

}
