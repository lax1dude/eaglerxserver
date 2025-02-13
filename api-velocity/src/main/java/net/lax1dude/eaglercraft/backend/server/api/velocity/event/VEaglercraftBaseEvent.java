package net.lax1dude.eaglercraft.backend.server.api.velocity.event;

import com.velocitypowered.api.proxy.Player;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglerXServerEvent;

class VEaglercraftBaseEvent implements IEaglerXServerEvent<Player> {

	private final IEaglerXServerAPI<Player> api;

	VEaglercraftBaseEvent(IEaglerXServerAPI<Player> api) {
		this.api = api;
	}

	@Override
	public IEaglerXServerAPI<Player> getServerAPI() {
		return api;
	}

}
