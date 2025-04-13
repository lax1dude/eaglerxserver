package net.lax1dude.eaglercraft.backend.server.bukkit.event;

import java.util.Map;

import org.bukkit.entity.Player;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.EaglercraftInitializePlayerEvent;

class BukkitInitializePlayerEventImpl extends EaglercraftInitializePlayerEvent {

	private final IEaglerXServerAPI<Player> api;
	private final IEaglerPlayer<Player> player;
	private final Map<String, byte[]> extraProfileData;

	BukkitInitializePlayerEventImpl(IEaglerXServerAPI<Player> api, IEaglerPlayer<Player> player,
			Map<String, byte[]> extraProfileData) {
		this.api = api;
		this.player = player;
		this.extraProfileData = extraProfileData;
	}

	@Override
	public IEaglerXServerAPI<Player> getServerAPI() {
		return api;
	}

	@Override
	public IEaglerPlayer<Player> getPlayer() {
		return player;
	}

	@Override
	public Map<String, byte[]> getExtraProfileData() {
		return extraProfileData;
	}

}
