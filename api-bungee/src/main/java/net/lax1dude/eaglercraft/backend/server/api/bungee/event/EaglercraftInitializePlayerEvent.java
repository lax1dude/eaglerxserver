package net.lax1dude.eaglercraft.backend.server.api.bungee.event;

import java.util.Map;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftInitializePlayerEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public final class EaglercraftInitializePlayerEvent extends Event
		implements IEaglercraftInitializePlayerEvent<ProxiedPlayer> {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private final IEaglerPlayer<ProxiedPlayer> player;
	private final Map<String, byte[]> extraProfileData;

	public EaglercraftInitializePlayerEvent(@Nonnull IEaglerXServerAPI<ProxiedPlayer> api,
			@Nonnull IEaglerPlayer<ProxiedPlayer> player, @Nonnull Map<String, byte[]> extraProfileData) {
		this.api = api;
		this.player = player;
		this.extraProfileData = extraProfileData;
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
	public Map<String, byte[]> getExtraProfileData() {
		return extraProfileData;
	}

}
