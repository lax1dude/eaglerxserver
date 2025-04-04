package net.lax1dude.eaglercraft.backend.rpc.bukkit.event;

import org.bukkit.entity.Player;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.bukkit.event.EaglercraftPlayerReadyEvent;

class BukkitPlayerReadyEventImpl extends EaglercraftPlayerReadyEvent {

	private final IEaglerXBackendRPC<Player> api;
	private final IEaglerPlayer<Player> player;

	BukkitPlayerReadyEventImpl(IEaglerXBackendRPC<Player> api, IEaglerPlayer<Player> player) {
		this.api = api;
		this.player = player;
	}

	@Override
	public IEaglerXBackendRPC<Player> getServerAPI() {
		return api;
	}

	@Override
	public IEaglerPlayer<Player> getPlayer() {
		return player;
	}

}
