package net.lax1dude.eaglercraft.backend.rpc.base.remote;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IBackendRPCPlayerInitializer;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayerInitializer;

class BackendRPCPlayerInitializer<PlayerObject> implements
		IBackendRPCPlayerInitializer<PlayerInstanceRemote<PlayerObject>, PlayerObject> {

	private final EaglerXBackendRPCRemote<PlayerObject> server;

	BackendRPCPlayerInitializer(EaglerXBackendRPCRemote<PlayerObject> server) {
		this.server = server;
	}

	@Override
	public void initializePlayer(IPlatformPlayerInitializer<PlayerInstanceRemote<PlayerObject>, PlayerObject> initializer) {
		PlayerInstanceRemote<PlayerObject> playerInstance = new PlayerInstanceRemote<>(server, initializer.getPlayer(),
				initializer.isEaglerPlayerProperty());
		initializer.setPlayerAttachment(playerInstance);
		server.registerPlayer(playerInstance);
	}

	@Override
	public void confirmPlayer(IPlatformPlayer<PlayerObject> player) {
		PlayerInstanceRemote<PlayerObject> playerInstance = player.getAttachment();
		if(playerInstance != null) {
			server.confirmPlayer(playerInstance);
		}
	}

	@Override
	public void destroyPlayer(IPlatformPlayer<PlayerObject> player) {
		PlayerInstanceRemote<PlayerObject> playerInstance = player.getAttachment();
		if(playerInstance != null) {
			server.unregisterPlayer(playerInstance);
		}
	}

}
