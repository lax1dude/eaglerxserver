package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebSocketEaglerFrameCodec extends MessageToMessageCodec<WebSocketFrame, ByteBuf> {

	@Override
	protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
		if(msg instanceof BinaryWebSocketFrame) {
			out.add(msg.content().retain());
		}else {
			// Text or close frames
			ctx.close();
		}
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
		out.add(new BinaryWebSocketFrame(msg.retain()));
	}

}
