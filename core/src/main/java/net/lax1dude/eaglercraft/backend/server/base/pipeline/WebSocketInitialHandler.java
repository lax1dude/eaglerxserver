package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.ReferenceCountUtil;
import net.lax1dude.eaglercraft.backend.server.adapter.PipelineAttributes;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;

@ChannelHandler.Sharable
public class WebSocketInitialHandler extends ChannelInboundHandlerAdapter {

	public static final WebSocketInitialHandler INSTANCE = new WebSocketInitialHandler();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			if(msg instanceof BinaryWebSocketFrame) {
				NettyPipelineData pipelineData = ctx.channel().attr(PipelineAttributes.<NettyPipelineData>pipelineData()).get();
				ctx.pipeline().addAfter(PipelineTransformer.HANDLER_WS_INITIAL, PipelineTransformer.HANDLER_HANDSHAKE,
						new WebSocketEaglerInitialHandler(pipelineData.server, pipelineData));
				ctx.fireChannelRead(((BinaryWebSocketFrame)msg).content().retain());
				ctx.pipeline().replace(PipelineTransformer.HANDLER_WS_INITIAL, PipelineTransformer.HANDLER_FRAME_CODEC,
						WebSocketEaglerFrameCodec.INSTANCE);
			}else if(msg instanceof TextWebSocketFrame) {
				NettyPipelineData pipelineData = ctx.channel().attr(PipelineAttributes.<NettyPipelineData>pipelineData()).get();
				pipelineData.server.getPipelineTransformer().removeVanillaHandlers(ctx.pipeline());
				ctx.pipeline().addAfter(PipelineTransformer.HANDLER_WS_INITIAL, PipelineTransformer.HANDLER_QUERY,
						new WebSocketQueryHandler(pipelineData.server, pipelineData));
				ctx.fireChannelRead(((TextWebSocketFrame)msg).retain());
				ctx.pipeline().remove(PipelineTransformer.HANDLER_WS_INITIAL);
			}else {
				ctx.close();
			}
		}finally {
			ReferenceCountUtil.release(msg);
		}
	}

}
