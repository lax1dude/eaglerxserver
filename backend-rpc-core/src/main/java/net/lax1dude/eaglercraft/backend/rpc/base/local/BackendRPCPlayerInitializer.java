package net.lax1dude.eaglercraft.backend.rpc.base.local;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IBackendRPCPlayerInitializer;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayerInitializer;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPreInitializer;
import net.lax1dude.eaglercraft.backend.server.api.IBasePlayer;

class BackendRPCPlayerInitializer<PlayerObject> implements
		IBackendRPCPlayerInitializer<PlayerInitData<PlayerObject>, BasePlayerLocal<PlayerObject>, PlayerObject> {

	private final EaglerXBackendRPCLocal<PlayerObject> server;

	BackendRPCPlayerInitializer(EaglerXBackendRPCLocal<PlayerObject> server) {
		this.server = server;
	}

	@Override
	public void initializePlayer(IPlatformPreInitializer<PlayerInitData<PlayerObject>, PlayerObject> initializer) {
		initializer.setPreAttachment(new PlayerInitData<>());
	}

	@Override
	public void confirmPlayer(IPlatformPlayerInitializer<PlayerInitData<PlayerObject>, BasePlayerLocal<PlayerObject>, PlayerObject> initializer) {
		PlayerInitData<PlayerObject> playerData = initializer.getPreAttachment();
		IPlatformPlayer<PlayerObject> player = initializer.getPlayer();
		if(playerData.eaglerPlayer != null) {
			EaglerPlayerLocal<PlayerObject> playerInstance = new EaglerPlayerLocal<>(server, player, playerData.eaglerPlayer);
			initializer.setPlayerAttachment(playerInstance);
			server.registerEaglerPlayer(playerInstance);
		}else {
			IBasePlayer<PlayerObject> basePlayer = server.serverAPI().getPlayer(player.getPlayerObject());
			if(basePlayer != null) {
				BasePlayerLocal<PlayerObject> playerInstance = new BasePlayerLocal<>(server, player, basePlayer);
				initializer.setPlayerAttachment(playerInstance);
				server.registerVanillaPlayer(playerInstance);
			}
		}
	}

	@Override
	public void destroyPlayer(IPlatformPlayer<PlayerObject> player) {
		BasePlayerLocal<PlayerObject> playerInstance = player.getAttachment();
		if(playerInstance != null) {
			if(playerInstance.isEaglerPlayer()) {
				server.unregisterEaglerPlayer((EaglerPlayerLocal<PlayerObject>)playerInstance);
			}else {
				server.unregisterVanillaPlayer(playerInstance);
			}
		}
	}

}
