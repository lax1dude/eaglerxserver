package net.lax1dude.eaglercraft.backend.server.api.bungee.event;

import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewMessageEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public final class EaglercraftWebViewMessageEvent extends Event
		implements IEaglercraftWebViewMessageEvent<ProxiedPlayer> {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private final IEaglerPlayer<ProxiedPlayer> player;
	private final String channel;
	private final EnumMessageType type;
	private final byte[] data;
	private String asString;

	public EaglercraftWebViewMessageEvent(@Nonnull IEaglerXServerAPI<ProxiedPlayer> api,
			@Nonnull IEaglerPlayer<ProxiedPlayer> player, @Nonnull String channel, @Nonnull EnumMessageType type,
			@Nonnull byte[] data) {
		this.api = api;
		this.player = player;
		this.channel = channel;
		this.type = type;
		this.data = data;
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
	public String getChannel() {
		return channel;
	}

	@Nonnull
	@Override
	public EnumMessageType getType() {
		return type;
	}

	@Nonnull
	@Override
	public String getAsString() {
		if(asString == null) {
			asString = new String(data, StandardCharsets.UTF_8);
		}
		return asString;
	}

	@Nonnull
	@Override
	public byte[] getAsBinary() {
		return data;
	}

}
