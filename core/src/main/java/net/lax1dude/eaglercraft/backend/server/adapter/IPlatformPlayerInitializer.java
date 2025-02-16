package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IPlatformPlayerInitializer<ConnectionAttachment, PlayerAttachment, PlayerObject> {

	IPlatformPlayer<PlayerObject> getPlayer();

	ConnectionAttachment getConnectionAttachment();

	void setPlayerAttachment(PlayerAttachment attachment);

}
