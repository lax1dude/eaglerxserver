package net.lax1dude.eaglercraft.backend.rpc.api.bukkit;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.internal.factory.EaglerXBackendRPCFactory;

public class EaglerXBackendRPC {

	@Nonnull
	public static IEaglerXBackendRPC<Player> instance() {
		return EaglerXBackendRPCFactory.INSTANCE.getAPI(Player.class);
	}

}
