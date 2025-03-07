package net.lax1dude.eaglercraft.backend.server.bukkit.event;

import org.bukkit.entity.Player;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.EaglercraftWebSocketOpenEvent;

class BukkitWebSocketOpenEventImpl extends EaglercraftWebSocketOpenEvent {

	private final IEaglerXServerAPI<Player> api;
	private boolean cancelled;
	private final IEaglerConnection connection;

	BukkitWebSocketOpenEventImpl(IEaglerXServerAPI<Player> api, IEaglerConnection connection) {
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
