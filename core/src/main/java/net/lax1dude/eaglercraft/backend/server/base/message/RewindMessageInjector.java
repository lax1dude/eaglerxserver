package net.lax1dude.eaglercraft.backend.server.base.message;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IOutboundInjector;

public class RewindMessageInjector implements IOutboundInjector {

	private final Channel channel;

	public RewindMessageInjector(Channel channel) {
		this.channel = channel;
	}

	@Override
	public void injectOutbound(IMessage msg) {
		if(channel.isActive()) {
			channel.writeAndFlush(msg);
		}
	}

}
