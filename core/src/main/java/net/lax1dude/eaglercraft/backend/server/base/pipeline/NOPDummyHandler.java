package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;

@ChannelHandler.Sharable
public class NOPDummyHandler extends ChannelHandlerAdapter {

	public static final NOPDummyHandler INSTANCE = new NOPDummyHandler();

	private NOPDummyHandler() {
	}

}
