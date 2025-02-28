package net.lax1dude.eaglercraft.backend.server.velocity;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
class VelocityCompressionDisablerHack extends ChannelInboundHandlerAdapter {

	static final VelocityCompressionDisablerHack INSTANCE = new VelocityCompressionDisablerHack();

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		ctx.fireUserEventTriggered(evt);
		if(VelocityUnsafe.isCompressionEnableEvent(evt)) {
			ChannelHandler handler = ctx.pipeline().get("handler");
			if(handler != null) {
				VelocityUnsafe.disableCompression(handler);
			}
			ctx.pipeline().remove(this);
		}
	}

}
