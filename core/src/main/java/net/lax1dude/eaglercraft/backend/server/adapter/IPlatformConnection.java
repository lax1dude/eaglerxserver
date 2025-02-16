package net.lax1dude.eaglercraft.backend.server.adapter;

import java.net.SocketAddress;
import java.util.UUID;

public interface IPlatformConnection {

	<T> T getAttachment();

	String getUsername();

	UUID getUniqueId();

	SocketAddress getSocketAddress();

	int getMinecraftProtocol();

	boolean isOnlineMode();

	void disconnect();

	<ComponentObject> void disconnect(ComponentObject kickMessage);

}
