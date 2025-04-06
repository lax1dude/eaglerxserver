package net.lax1dude.eaglercraft.backend.server.api.bukkit;

import org.bukkit.entity.Player;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.internal.factory.EaglerXServerAPIFactory;

public final class EaglerXServerAPI {

	public static IEaglerXServerAPI<Player> instance() {
		return EaglerXServerAPIFactory.INSTANCE.getAPI(Player.class);
	}

	private EaglerXServerAPI() {
	}

}
