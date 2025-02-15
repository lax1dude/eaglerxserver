package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IPlatformPlayer<PlayerObject> {

	IPlatformConnection getConnection();

	PlayerObject getPlayerObject();

	<T> T getPlayerAttachment();

	default <T> T getConnectionAttachment() {
		return getConnection().getAttachment();
	}

}
