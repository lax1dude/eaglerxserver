package net.lax1dude.eaglercraft.backend.server.velocity.event;

import com.velocitypowered.api.proxy.Player;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.velocity.event.EaglercraftInitializePlayerEvent;

class VelocityInitializePlayerEventImpl extends EaglercraftInitializePlayerEvent {

	private final IEaglerXServerAPI<Player> api;
	private final IEaglerPlayer<Player> player;

	VelocityInitializePlayerEventImpl(IEaglerXServerAPI<Player> api, IEaglerPlayer<Player> player) {
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
