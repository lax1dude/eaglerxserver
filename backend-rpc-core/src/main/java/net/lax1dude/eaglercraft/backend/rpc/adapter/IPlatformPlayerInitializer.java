package net.lax1dude.eaglercraft.backend.rpc.adapter;

public interface IPlatformPlayerInitializer<PreAttachment, PlayerAttachment, PlayerObject> {

	PreAttachment getPreAttachment();

	void setPlayerAttachment(PlayerAttachment attachment);

	IPlatformPlayer<PlayerObject> getPlayer();

}
