package net.lax1dude.eaglercraft.backend.server.adapter;

import java.util.UUID;

public interface IPlatformPlayer<PlayerObject> {

	IPlatformConnection getConnection();

	PlayerObject getPlayerObject();

	String getUsername();

	UUID getUniqueId();

	boolean isConnected();

	boolean isOnlineMode();

	String getMinecraftBrand();

	<T> T getPlayerAttachment();

	default <T> T getConnectionAttachment() {
		return getConnection().getAttachment();
	}

}
