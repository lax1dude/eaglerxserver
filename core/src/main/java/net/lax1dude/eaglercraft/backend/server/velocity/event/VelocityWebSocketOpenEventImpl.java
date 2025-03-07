package net.lax1dude.eaglercraft.backend.server.velocity.event;

import com.velocitypowered.api.proxy.Player;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.velocity.event.EaglercraftWebSocketOpenEvent;

class VelocityWebSocketOpenEventImpl extends EaglercraftWebSocketOpenEvent {

	private final IEaglerXServerAPI<Player> api;
	private boolean cancelled;
	private final IEaglerConnection connection;

	VelocityWebSocketOpenEventImpl(IEaglerXServerAPI<Player> api, IEaglerConnection connection) {
		this.api = api;
		this.connection = connection;
	}

	@Override
	public IEaglerXServerAPI<Player> getServerAPI() {
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
