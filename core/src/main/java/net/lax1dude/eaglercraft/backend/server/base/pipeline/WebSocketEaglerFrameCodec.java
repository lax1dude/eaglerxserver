package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

@ChannelHandler.Sharable
public class WebSocketEaglerFrameCodec extends ChannelDuplexHandler {

	public static final WebSocketEaglerFrameCodec INSTANCE = new WebSocketEaglerFrameCodec();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof BinaryWebSocketFrame) {
			ctx.fireChannelRead(((BinaryWebSocketFrame)msg).content());
		}else if(msg instanceof WebSocketFrame) {
			// Text or close frames
			((WebSocketFrame)msg).release();
			ctx.close();
		}else {
			ctx.fireChannelRead(msg);
		}
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if(msg instanceof ByteBuf) {
			ByteBuf buf = (ByteBuf) msg;
			if(buf.readableBytes() > 0) {
				ctx.write(new BinaryWebSocketFrame(buf), promise);
				return;
			}
		}
		ctx.write(msg, promise);
	}

}
