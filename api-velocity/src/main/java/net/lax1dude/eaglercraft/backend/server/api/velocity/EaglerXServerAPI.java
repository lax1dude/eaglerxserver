package net.lax1dude.eaglercraft.backend.server.api.velocity;

import com.velocitypowered.api.proxy.Player;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.internal.factory.EaglerXServerAPIFactory;

public class EaglerXServerAPI {

	public static final String PLUGIN_ID = "eaglerxserver";

	public static IEaglerXServerAPI<Player> instance() {
		return EaglerXServerAPIFactory.INSTANCE.getAPI(Player.class);
	}

}
