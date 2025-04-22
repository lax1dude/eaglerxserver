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
import io.netty.util.ReferenceCountUtil;
import net.lax1dude.eaglercraft.backend.server.util.Util;

public class WebSocketPingFrameHandler extends ChannelInboundHandlerAdapter {

	private long nextPing = 0l;
	private int pingQuota = 3;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof PingWebSocketFrame) {
			ReferenceCountUtil.release(msg);
			long now = Util.steadyTime();
			if(now > nextPing) {
				pingQuota = 3;
				nextPing = now + 1000l;
			}
			if(pingQuota > 0) {
				--pingQuota;
				ctx.write(new PongWebSocketFrame());
			}
		}else if(!(msg instanceof PongWebSocketFrame)) {
			ctx.fireChannelRead(msg);
		}else {
			ReferenceCountUtil.release(msg);
		}
	}

}
