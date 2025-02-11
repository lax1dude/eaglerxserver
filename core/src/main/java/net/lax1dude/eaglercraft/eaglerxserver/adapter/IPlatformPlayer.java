package net.lax1dude.eaglercraft.eaglerxserver.adapter;

public interface IPlatformPlayer {

	IPlatformConnection getConnection();

	<T> T getPlayerAttachment();

	default <T> T getConnectionAttachment() {
		return getConnection().getAttachment();
	}

}
