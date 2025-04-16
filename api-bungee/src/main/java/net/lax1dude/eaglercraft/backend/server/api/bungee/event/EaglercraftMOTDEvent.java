package net.lax1dude.eaglercraft.backend.server.api.bungee.event;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftMOTDEvent;
import net.lax1dude.eaglercraft.backend.server.api.query.IMOTDConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public final class EaglercraftMOTDEvent extends Event implements IEaglercraftMOTDEvent<ProxiedPlayer> {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private final IMOTDConnection connection;

	public EaglercraftMOTDEvent(@Nonnull IEaglerXServerAPI<ProxiedPlayer> api, @Nonnull IMOTDConnection connection) {
		this.api = api;
		this.connection = connection;
	}

	@Nonnull
	@Override
	public IEaglerXServerAPI<ProxiedPlayer> getServerAPI() {
		return api;
	}

	@Nonnull
	@Override
	public IMOTDConnection getMOTDConnection() {
		return connection;
	}

}
