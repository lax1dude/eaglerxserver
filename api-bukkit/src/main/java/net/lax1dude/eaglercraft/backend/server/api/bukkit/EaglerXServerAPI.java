package net.lax1dude.eaglercraft.backend.server.api.bukkit;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.internal.factory.EaglerXServerAPIFactory;

public final class EaglerXServerAPI {

	@Nonnull
	public static IEaglerXServerAPI<Player> instance() {
		return EaglerXServerAPIFactory.INSTANCE.getAPI(Player.class);
	}

	private EaglerXServerAPI() {
	}

}
