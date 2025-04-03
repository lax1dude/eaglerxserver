package net.lax1dude.eaglercraft.backend.rpc.adapter;

public interface IPlatformPreInitializer<PreAttachment, PlayerObject> {

	void setPreAttachment(PreAttachment attachment);

	IPlatformPlayer<PlayerObject> getPlayer();

}
