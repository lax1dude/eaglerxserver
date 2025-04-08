package net.lax1dude.eaglercraft.backend.server.adapter;

import java.util.UUID;

public interface IPlatformPlayer<PlayerObject> extends IPlatformCommandSender<PlayerObject> {

	IPlatformConnection getConnection();

	PlayerObject getPlayerObject();

	IPlatformServer<PlayerObject> getServer();

	String getUsername();

	UUID getUniqueId();

	boolean isConnected();

	boolean isOnlineMode();

	String getMinecraftBrand();

	String getTexturesProperty();

	void sendDataClient(String channel, byte[] message);

	void sendDataBackend(String channel, byte[] message);

	boolean isSetViewDistanceSupportedPaper();

	void setViewDistancePaper(int distance);

	void disconnect();

	<ComponentObject> void disconnect(ComponentObject kickMessage);

	<T> T getPlayerAttachment();

	default <T> T getConnectionAttachment() {
		return getConnection().getAttachment();
	}

}
