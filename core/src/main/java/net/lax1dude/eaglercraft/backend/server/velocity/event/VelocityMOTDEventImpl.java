package net.lax1dude.eaglercraft.backend.server.velocity.event;

import com.velocitypowered.api.proxy.Player;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.query.IMOTDConnection;
import net.lax1dude.eaglercraft.backend.server.api.velocity.event.EaglercraftMOTDEvent;

class VelocityMOTDEventImpl extends EaglercraftMOTDEvent {

	private final IEaglerXServerAPI<Player> api;
	private final IMOTDConnection connection;

	VelocityMOTDEventImpl(IEaglerXServerAPI<Player> api, IMOTDConnection connection) {
		this.api = api;
		this.connection = connection;
	}

	@Override
	public IEaglerXServerAPI<Player> getServerAPI() {
		return api;
	}

	@Override
	public IMOTDConnection getMOTDConnection() {
		return connection;
	}

}
