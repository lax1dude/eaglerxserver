package net.lax1dude.eaglercraft.backend.rpc.adapter;

public interface IBackendRPCPlayerInitializer<PlayerAttachment, PlayerObject> {

	void initializePlayer(IPlatformPlayerInitializer<PlayerAttachment, PlayerObject> initializer);

	void confirmPlayer(IPlatformPlayer<PlayerObject> player);

	void destroyPlayer(IPlatformPlayer<PlayerObject> player);

}
