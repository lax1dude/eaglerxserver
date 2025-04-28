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
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.ReferenceCountUtil;
import net.lax1dude.eaglercraft.backend.server.adapter.PipelineAttributes;
import net.lax1dude.eaglercraft.backend.server.api.EnumPipelineEvent;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;

@ChannelHandler.Sharable
public class WebSocketInitialHandler extends ChannelInboundHandlerAdapter {

	public static final WebSocketInitialHandler INSTANCE = new WebSocketInitialHandler();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			if (!ctx.channel().isActive()) {
				return;
			}
			if (msg instanceof BinaryWebSocketFrame msg2) {
				NettyPipelineData pipelineData = ctx.channel()
						.attr(PipelineAttributes.<NettyPipelineData>pipelineData()).get();
				if (pipelineData.initStall) {
					return;
				}
				if (!pipelineData.processRealAddress()) {
					pipelineData.initStall = true;
					ctx.close();
					return;
				}
				if (!pipelineData.processLoginRatelimit(ctx)) {
					pipelineData.initStall = true;
					return;
				}
				ChannelPipeline pipeline = ctx.pipeline();
				pipeline.addAfter(PipelineTransformer.HANDLER_WS_INITIAL, PipelineTransformer.HANDLER_HANDSHAKE,
						new WebSocketEaglerInitialHandler(pipelineData.server, pipelineData));
				pipeline.fireUserEventTriggered(EnumPipelineEvent.EAGLER_STATE_WEBSOCKET_PLAYER);
				pipeline.replace(PipelineTransformer.HANDLER_WS_INITIAL, PipelineTransformer.HANDLER_FRAME_CODEC,
						WebSocketEaglerFrameCodec.INSTANCE);
				pipeline.fireUserEventTriggered(EnumPipelineEvent.EAGLER_INJECTED_FRAME_HANDLERS);
				ctx.fireChannelRead(msg2.content().retain());
			} else if (msg instanceof TextWebSocketFrame msg2) {
				NettyPipelineData pipelineData = ctx.channel()
						.attr(PipelineAttributes.<NettyPipelineData>pipelineData()).get();
				if (pipelineData.initStall) {
					return;
				}
				if (!pipelineData.processQueryRatelimit(ctx)) {
					pipelineData.initStall = true;
					return;
				}
				ChannelPipeline pipeline = ctx.pipeline();
				pipelineData.server.getPipelineTransformer().removeVanillaHandlers(pipeline);
				pipeline.replace(PipelineTransformer.HANDLER_WS_INITIAL, PipelineTransformer.HANDLER_QUERY,
						new WebSocketQueryHandler(pipelineData.server, pipelineData));
				pipeline.fireUserEventTriggered(EnumPipelineEvent.EAGLER_STATE_WEBSOCKET_QUERY);
				ctx.fireChannelRead(msg2.retain());
			} else {
				ctx.close();
			}
		} finally {
			ReferenceCountUtil.release(msg);
		}
	}

}
