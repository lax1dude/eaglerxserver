package net.lax1dude.eaglercraft.backend.server.bungee.event;

import java.util.Map;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftInitializePlayerEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

class BungeeInitializePlayerEventImpl extends EaglercraftInitializePlayerEvent {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private final IEaglerPlayer<ProxiedPlayer> player;
	private final Map<String, byte[]> extraProfileData;

	BungeeInitializePlayerEventImpl(IEaglerXServerAPI<ProxiedPlayer> api, IEaglerPlayer<ProxiedPlayer> player,
			Map<String, byte[]> extraProfileData) {
		this.api = api;
		this.player = player;
		this.extraProfileData = extraProfileData;
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
	public Map<String, byte[]> getExtraProfileData() {
		return extraProfileData;
	}

}
