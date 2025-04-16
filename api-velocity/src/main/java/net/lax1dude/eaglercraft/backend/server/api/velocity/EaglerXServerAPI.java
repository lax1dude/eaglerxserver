package net.lax1dude.eaglercraft.backend.server.api.velocity;

import javax.annotation.Nonnull;

import com.velocitypowered.api.proxy.Player;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.internal.factory.EaglerXServerAPIFactory;

public final class EaglerXServerAPI {

	@Nonnull
	public static final String PLUGIN_ID = "eaglerxserver";

	@Nonnull
	public static IEaglerXServerAPI<Player> instance() {
		return EaglerXServerAPIFactory.INSTANCE.getAPI(Player.class);
	}

	private EaglerXServerAPI() {
	}

}
