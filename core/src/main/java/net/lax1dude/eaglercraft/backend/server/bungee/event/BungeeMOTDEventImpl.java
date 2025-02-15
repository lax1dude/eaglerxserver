package net.lax1dude.eaglercraft.backend.server.bungee.event;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftMOTDEvent;
import net.lax1dude.eaglercraft.backend.server.api.query.IMOTDConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;

class BungeeMOTDEventImpl extends EaglercraftMOTDEvent {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private final IMOTDConnection connection;

	BungeeMOTDEventImpl(IEaglerXServerAPI<ProxiedPlayer> api, IMOTDConnection connection) {
		this.api = api;
		this.connection = connection;
	}

	@Override
	public IEaglerXServerAPI<ProxiedPlayer> getServerAPI() {
		return api;
	}

	@Override
	public IMOTDConnection getMOTDConnection() {
		return connection;
	}

}
