package net.lax1dude.eaglercraft.backend.rpc.base.local;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IBackendRPCPlayerInitializer;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayerInitializer;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPreInitializer;

class BackendRPCPlayerInitializer<PlayerObject> implements
		IBackendRPCPlayerInitializer<PlayerInitData<PlayerObject>, BasePlayerLocal<PlayerObject>, PlayerObject> {

	private final EaglerXBackendRPCLocal<PlayerObject> server;

	BackendRPCPlayerInitializer(EaglerXBackendRPCLocal<PlayerObject> server) {
		this.server = server;
	}

	@Override
	public void initializePlayer(IPlatformPreInitializer<PlayerInitData<PlayerObject>, PlayerObject> initializer) {
	}

	@Override
	public void confirmPlayer(IPlatformPlayerInitializer<PlayerInitData<PlayerObject>, BasePlayerLocal<PlayerObject>, PlayerObject> initializer) {
		
	}

	@Override
	public void destroyPlayer(IPlatformPlayer<PlayerObject> player) {
		
	}

}
