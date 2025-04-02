package net.lax1dude.eaglercraft.backend.rpc.api.bukkit;

import org.bukkit.entity.Player;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXServerRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.internal.factory.EaglerXServerRPCFactory;

public class EaglerXServerRPC {

	public static IEaglerXServerRPC<Player> instance() {
		return EaglerXServerRPCFactory.INSTANCE.getAPI(Player.class);
	}

}
