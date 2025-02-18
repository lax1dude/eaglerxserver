package net.lax1dude.eaglercraft.backend.server.bukkit.event;

import org.bukkit.entity.Player;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.EaglercraftDestroyPlayerEvent;

class BukkitDestroyPlayerEventImpl extends EaglercraftDestroyPlayerEvent {

	private final IEaglerXServerAPI<Player> api;
	private final IEaglerPlayer<Player> player;

	BukkitDestroyPlayerEventImpl(IEaglerXServerAPI<Player> api, IEaglerPlayer<Player> player) {
		this.api = api;
		this.player = player;
	}

	@Override
	public IEaglerXServerAPI<Player> getServerAPI() {
		return api;
	}

	@Override
	public IEaglerPlayer<Player> getPlayer() {
		return player;
	}

}
