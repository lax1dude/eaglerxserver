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
			throw new IllegalStateException("Server sent an unexpected packet before the connection was initialized");
		}else {
			ctx.write(msg, promise);
		}
	}

}
