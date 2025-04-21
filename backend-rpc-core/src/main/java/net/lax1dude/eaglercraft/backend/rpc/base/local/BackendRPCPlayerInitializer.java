package net.lax1dude.eaglercraft.backend.rpc.base.local;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IBackendRPCPlayerInitializer;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayerInitializer;

class BackendRPCPlayerInitializer<PlayerObject> implements
		IBackendRPCPlayerInitializer<PlayerInstanceLocal<PlayerObject>, PlayerObject> {

	private final EaglerXBackendRPCLocal<PlayerObject> server;

	BackendRPCPlayerInitializer(EaglerXBackendRPCLocal<PlayerObject> server) {
		this.server = server;
	}

	@Override
	public void initializePlayer(IPlatformPlayerInitializer<PlayerInstanceLocal<PlayerObject>, PlayerObject> initializer) {
		PlayerInstanceLocal<PlayerObject> playerInstance = new PlayerInstanceLocal<PlayerObject>(server, initializer.getPlayer());
		initializer.setPlayerAttachment(playerInstance);
		server.registerPlayer(playerInstance);
		
	}

	@Override
	public void confirmPlayer(IPlatformPlayer<PlayerObject> player) {
		PlayerInstanceLocal<PlayerObject> playerInstance = player.getAttachment();
		if(playerInstance != null) {
			server.confirmPlayer(playerInstance);
		}
	}

	@Override
	public void destroyPlayer(IPlatformPlayer<PlayerObject> player) {
		PlayerInstanceLocal<PlayerObject> playerInstance = player.getAttachment();
		if(playerInstance != null) {
			server.unregisterPlayer(playerInstance);
		}
	}

}
