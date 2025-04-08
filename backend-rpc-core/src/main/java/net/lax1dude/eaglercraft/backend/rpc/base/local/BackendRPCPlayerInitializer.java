package net.lax1dude.eaglercraft.backend.rpc.base.local;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IBackendRPCPlayerInitializer;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayerInitializer;

class BackendRPCPlayerInitializer<PlayerObject> implements
		IBackendRPCPlayerInitializer<BasePlayerLocal<PlayerObject>, PlayerObject> {

	private final EaglerXBackendRPCLocal<PlayerObject> server;

	BackendRPCPlayerInitializer(EaglerXBackendRPCLocal<PlayerObject> server) {
		this.server = server;
	}

	@Override
	public void initializePlayer(IPlatformPlayerInitializer<BasePlayerLocal<PlayerObject>, PlayerObject> initializer) {
		net.lax1dude.eaglercraft.backend.server.api.IBasePlayer<PlayerObject> player = server.serverAPI()
				.getPlayer(initializer.getPlayer().getPlayerObject());
		if(player.isEaglerPlayer()) {
			EaglerPlayerLocal<PlayerObject> playerInstance = new EaglerPlayerLocal<>(server, initializer.getPlayer(),
					player.asEaglerPlayer());
			initializer.setPlayerAttachment(playerInstance);
			server.registerEaglerPlayer(playerInstance);
		}else {
			BasePlayerLocal<PlayerObject> playerInstance = new BasePlayerLocal<>(server, initializer.getPlayer(), player);
			initializer.setPlayerAttachment(playerInstance);
			server.registerVanillaPlayer(playerInstance);
		}
	}

	@Override
	public void confirmPlayer(IPlatformPlayer<PlayerObject> player) {
		BasePlayerLocal<PlayerObject> playerInstance = player.getAttachment();
		if(playerInstance != null) {
			if(playerInstance instanceof EaglerPlayerLocal<PlayerObject> playerInstanceLocal) {
				server.confirmEaglerPlayer(playerInstanceLocal);
			}else {
				server.confirmVanillaPlayer(playerInstance);
			}
		}
	}

	@Override
	public void destroyPlayer(IPlatformPlayer<PlayerObject> player) {
		BasePlayerLocal<PlayerObject> playerInstance = player.getAttachment();
		if(playerInstance != null) {
			if(playerInstance instanceof EaglerPlayerLocal<PlayerObject> playerInstanceLocal) {
				server.unregisterEaglerPlayer(playerInstanceLocal);
			}else {
				server.unregisterVanillaPlayer(playerInstance);
			}
		}
	}

}
