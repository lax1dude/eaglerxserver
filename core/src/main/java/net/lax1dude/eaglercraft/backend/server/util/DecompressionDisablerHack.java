package net.lax1dude.eaglercraft.backend.server.util;

import java.util.function.Consumer;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;

@ChannelHandler.Sharable
public class DecompressionDisablerHack extends ChannelInboundHandlerAdapter {

	private final String channelName;
	private final Consumer<ChannelHandler> bukkitDisposer;

	public DecompressionDisablerHack(String channelName, Consumer<ChannelHandler> bukkitDisposer) {
		this.channelName = channelName;
		this.bukkitDisposer = bukkitDisposer;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ChannelPipeline p = ctx.pipeline();
		ChannelHandler h = p.remove(channelName);
		if(h != null && bukkitDisposer != null) {
			bukkitDisposer.accept(h);
		}
		ctx.fireChannelRead(msg);
		p.remove(this);
	}

}
