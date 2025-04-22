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

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.ReferenceCountUtil;

@ChannelHandler.Sharable
public class OutboundPacketThrowHandler extends ChannelOutboundHandlerAdapter {

	public static final OutboundPacketThrowHandler INSTANCE = new OutboundPacketThrowHandler();

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if(!(msg instanceof HttpResponse) && !(msg instanceof WebSocketFrame)) {
			ReferenceCountUtil.release(msg);
			ctx.close();
			promise.setFailure(new IllegalStateException("Server sent an unexpected packet before the connection was initialized"));
		}else {
			ctx.write(msg, promise);
		}
	}

}
