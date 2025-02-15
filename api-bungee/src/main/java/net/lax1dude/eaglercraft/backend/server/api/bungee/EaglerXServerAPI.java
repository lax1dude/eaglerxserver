package net.lax1dude.eaglercraft.backend.server.api.bungee;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class EaglerXServerAPI {

	public static IEaglerXServerAPI<ProxiedPlayer> instance() {
		return IEaglerXServerAPI.instance(ProxiedPlayer.class);
	}

}
