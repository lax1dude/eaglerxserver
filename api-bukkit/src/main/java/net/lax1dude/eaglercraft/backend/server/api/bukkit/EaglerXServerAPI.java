package net.lax1dude.eaglercraft.backend.server.api.bukkit;

import org.bukkit.entity.Player;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;

public class EaglerXServerAPI {

	public static IEaglerXServerAPI<Player> instance() {
		return IEaglerXServerAPI.instance(Player.class);
	}

}
