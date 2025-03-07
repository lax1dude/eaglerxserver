package net.lax1dude.eaglercraft.backend.server.api;

import java.net.SocketAddress;

import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeHolder;

public interface IBaseConnection extends INettyChannel, IAttributeHolder {

	SocketAddress getSocketAddress();

	String getRealAddress();

	boolean isConnected();

	void disconnect();

}
