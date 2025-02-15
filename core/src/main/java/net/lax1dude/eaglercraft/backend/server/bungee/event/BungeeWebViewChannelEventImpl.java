package net.lax1dude.eaglercraft.backend.server.bungee.event;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftWebViewChannelEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

class BungeeWebViewChannelEventImpl extends EaglercraftWebViewChannelEvent {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private final IEaglerPlayer<ProxiedPlayer> player;
	private final EnumEventType type;
	private final String channel;

	BungeeWebViewChannelEventImpl(IEaglerXServerAPI<ProxiedPlayer> api, IEaglerPlayer<ProxiedPlayer> player,
			EnumEventType type, String channel) {
		this.api = api;
		this.player = player;
		this.type = type;
		this.channel = channel;
	}

	@Override
	public IEaglerXServerAPI<ProxiedPlayer> getServerAPI() {
		return api;
	}

	@Override
	public IEaglerPlayer<ProxiedPlayer> getPlayer() {
		return player;
	}

	@Override
	public EnumEventType getType() {
		return type;
	}

	@Override
	public String getChannel() {
		return channel;
	}

}
