package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IEaglerXServerPlayerInitializer<ConnectionAttachment, PlayerAttachment, PlayerObject> {

	void initializePlayer(IPlatformPlayerInitializer<ConnectionAttachment, PlayerAttachment, PlayerObject> initializer);

	void destroyPlayer(IPlatformPlayer<PlayerObject> player);

}
