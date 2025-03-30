package net.lax1dude.eaglercraft.backend.server.base.rpc;

import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;

public class BackendRPCService<PlayerObject> {

	private final EaglerXServer<PlayerObject> server;

	public BackendRPCService(EaglerXServer<PlayerObject> server) {
		this.server = server;
	}

	public VanillaPlayerRPCManager<PlayerObject> createVanillaPlayerRPCManager(BasePlayerInstance<PlayerObject> player) {
		return new VanillaPlayerRPCManager<>(player);
	}

	public EaglerPlayerRPCManager<PlayerObject> createEaglerPlayerRPCManager(EaglerPlayerInstance<PlayerObject> player) {
		return new EaglerPlayerRPCManager<>(player);
	}

}
