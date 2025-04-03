package net.lax1dude.eaglercraft.backend.rpc.adapter;

public interface IBackendRPCPlayerInitializer<PreAttachment, PlayerAttachment, PlayerObject> {

	void initializePlayer(IPlatformPreInitializer<PreAttachment, PlayerObject> initializer);

	void confirmPlayer(IPlatformPlayerInitializer<PreAttachment, PlayerAttachment, PlayerObject> initializer);

	void destroyPlayer(IPlatformPlayer<PlayerObject> player);

}
