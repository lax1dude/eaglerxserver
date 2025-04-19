package net.lax1dude.eaglercraft.backend.server.bukkit.async;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.PlayerLoginInitEvent;

class PlayerLoginInitEventImpl extends PlayerLoginInitEvent implements PlayerLoginInitEvent.NettyUnsafe {

	private final Channel channel;

	PlayerLoginInitEventImpl(Channel channel) {
		this.channel = channel;
	}

	@Override
	public NettyUnsafe netty() {
		return this;
	}

	@Override
	public Channel getChannel() {
		return channel;
	}

}
