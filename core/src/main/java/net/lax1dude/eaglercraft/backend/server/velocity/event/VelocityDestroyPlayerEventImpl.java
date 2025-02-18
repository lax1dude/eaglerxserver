package net.lax1dude.eaglercraft.backend.server.velocity.event;

import com.velocitypowered.api.proxy.Player;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.velocity.event.EaglercraftDestroyPlayerEvent;

class VelocityDestroyPlayerEventImpl extends EaglercraftDestroyPlayerEvent {

	private final IEaglerXServerAPI<Player> api;
	private final IEaglerPlayer<Player> player;

	VelocityDestroyPlayerEventImpl(IEaglerXServerAPI<Player> api, IEaglerPlayer<Player> player) {
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
