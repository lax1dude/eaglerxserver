package net.lax1dude.eaglercraft.backend.server.api;

import java.net.SocketAddress;
import java.util.UUID;

public interface IBasePendingConnection extends IAttributeHolder {

	String getUsername();

	UUID getUniqueId();

	SocketAddress getSocketAddress();

	String getRealAddress();

	int getMinecraftProtocol();

	boolean isEaglerPlayer();

	void disconnect();

}
