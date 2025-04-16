package net.lax1dude.eaglercraft.backend.server.api.bungee;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.internal.factory.EaglerXServerAPIFactory;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public final class EaglerXServerAPI {

	@Nonnull
	public static IEaglerXServerAPI<ProxiedPlayer> instance() {
		return EaglerXServerAPIFactory.INSTANCE.getAPI(ProxiedPlayer.class);
	}

	private EaglerXServerAPI() {
	}

}
