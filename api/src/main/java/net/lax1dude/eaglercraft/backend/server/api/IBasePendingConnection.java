package net.lax1dude.eaglercraft.backend.server.api;

import java.net.SocketAddress;

import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeHolder;

public interface IBasePendingConnection extends IAttributeHolder {

	SocketAddress getSocketAddress();

	String getRealAddress();

	int getMinecraftProtocol();

	boolean isEaglerPlayer();

	IEaglerPendingConnection asEaglerPlayer();

	boolean isOnlineMode();

	void disconnect();

}
