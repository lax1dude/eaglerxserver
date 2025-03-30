package net.lax1dude.eaglercraft.backend.server.api;

import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeHolder;

public interface IBaseConnection extends INettyChannel, IAttributeHolder {

	boolean isConnected();

	void disconnect();

}
