package net.lax1dude.eaglercraft.backend.server.api.velocity;

import com.velocitypowered.api.proxy.Player;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;

public class VEaglerXServerAPI {

	public static IEaglerXServerAPI<Player> instance() {
		return IEaglerXServerAPI.instance(Player.class);
	}

}
