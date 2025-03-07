package net.lax1dude.eaglercraft.backend.server.bungee.event;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftWebSocketOpenEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

class BungeeWebSocketOpenEventImpl extends EaglercraftWebSocketOpenEvent {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private boolean cancelled;
	private final IEaglerConnection connection;

	BungeeWebSocketOpenEventImpl(IEaglerXServerAPI<ProxiedPlayer> api, IEaglerConnection connection) {
		this.api = api;
		this.connection = connection;
	}

	@Override
	public IEaglerXServerAPI<ProxiedPlayer> getServerAPI() {
		return api;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public IEaglerConnection getConnection() {
		return connection;
	}

}
