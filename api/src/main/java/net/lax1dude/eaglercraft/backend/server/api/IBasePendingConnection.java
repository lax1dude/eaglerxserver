package net.lax1dude.eaglercraft.backend.server.api;

import java.net.SocketAddress;

public interface IBasePendingConnection extends IBaseConnection {

	int getMinecraftProtocol();

	SocketAddress getPlayerAddress();

	boolean isEaglerPlayer();

	IEaglerPendingConnection asEaglerPlayer();

}
