package net.lax1dude.eaglercraft.backend.server.util;

import java.util.function.Consumer;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;

@ChannelHandler.Sharable
public class CompressionDisablerHack extends ChannelOutboundHandlerAdapter {

	private final String channelName;
	private final Consumer<ChannelHandler> bukkitDisposer;

	public CompressionDisablerHack(String channelName, Consumer<ChannelHandler> bukkitDisposer) {
		this.channelName = channelName;
		this.bukkitDisposer = bukkitDisposer;
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		ChannelPipeline p = ctx.pipeline();
		ChannelHandler h = p.remove(channelName);
		if(h != null && bukkitDisposer != null) {
			bukkitDisposer.accept(h);
		}
		ctx.write(msg, promise);
		p.remove(this);
	}

}
