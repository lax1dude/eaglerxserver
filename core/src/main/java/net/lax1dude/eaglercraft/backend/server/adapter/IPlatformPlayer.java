package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IPlatformPlayer {

	IPlatformConnection getConnection();

	<T> T getPlayerAttachment();

	default <T> T getConnectionAttachment() {
		return getConnection().getAttachment();
	}

}
