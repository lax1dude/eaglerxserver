package net.lax1dude.eaglercraft.backend.server.api.bungee.event;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewChannelEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public final class EaglercraftWebViewChannelEvent extends Event
		implements IEaglercraftWebViewChannelEvent<ProxiedPlayer> {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private final IEaglerPlayer<ProxiedPlayer> player;
	private final EnumEventType type;
	private final String channel;

	public EaglercraftWebViewChannelEvent(@Nonnull IEaglerXServerAPI<ProxiedPlayer> api,
			@Nonnull IEaglerPlayer<ProxiedPlayer> player, @Nonnull EnumEventType type, @Nonnull String channel) {
		this.api = api;
		this.player = player;
		this.type = type;
		this.channel = channel;
	}

	@Nonnull
	@Override
	public IEaglerXServerAPI<ProxiedPlayer> getServerAPI() {
		return api;
	}

	@Nonnull
	@Override
	public IEaglerPlayer<ProxiedPlayer> getPlayer() {
		return player;
	}

	@Nonnull
	@Override
	public EnumEventType getType() {
		return type;
	}

	@Nonnull
	@Override
	public String getChannel() {
		return channel;
	}

}
