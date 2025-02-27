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
