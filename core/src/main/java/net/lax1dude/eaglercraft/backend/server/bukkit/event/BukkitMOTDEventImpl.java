package net.lax1dude.eaglercraft.backend.server.bukkit.event;

import org.bukkit.entity.Player;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.EaglercraftMOTDEvent;
import net.lax1dude.eaglercraft.backend.server.api.query.IMOTDConnection;

class BukkitMOTDEventImpl extends EaglercraftMOTDEvent {

	private final IEaglerXServerAPI<Player> api;
	private final IMOTDConnection connection;

	BukkitMOTDEventImpl(IEaglerXServerAPI<Player> api, IMOTDConnection connection) {
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
