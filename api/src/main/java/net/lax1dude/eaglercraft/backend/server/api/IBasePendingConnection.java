package net.lax1dude.eaglercraft.backend.server.api;

import java.net.SocketAddress;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeHolder;

public interface IBasePendingConnection extends IAttributeHolder {

	String getUsername();

	UUID getUniqueId();

	SocketAddress getSocketAddress();

	String getRealAddress();

	int getMinecraftProtocol();

	boolean isEaglerPlayer();

	IEaglerPendingConnection asEaglerPlayer();

	boolean isOnlineMode();

	void disconnect();

}
