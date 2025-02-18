package net.lax1dude.eaglercraft.backend.server.bungee.event;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftDestroyPlayerEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

class BungeeDestroyPlayerEventImpl extends EaglercraftDestroyPlayerEvent {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private final IEaglerPlayer<ProxiedPlayer> player;

	BungeeDestroyPlayerEventImpl(IEaglerXServerAPI<ProxiedPlayer> api, IEaglerPlayer<ProxiedPlayer> player) {
		this.api = api;
		this.player = player;
	}

	@Override
	public IEaglerXServerAPI<ProxiedPlayer> getServerAPI() {
		return api;
	}

	@Override
	public IEaglerPlayer<ProxiedPlayer> getPlayer() {
		return player;
	}

}
