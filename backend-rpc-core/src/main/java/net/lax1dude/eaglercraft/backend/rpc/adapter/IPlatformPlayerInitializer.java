package net.lax1dude.eaglercraft.backend.rpc.adapter;

public interface IPlatformPlayerInitializer<PlayerAttachment, PlayerObject> {

	void setPlayerAttachment(PlayerAttachment attachment);

	IPlatformPlayer<PlayerObject> getPlayer();

	boolean isEaglerPlayerProperty();

}
