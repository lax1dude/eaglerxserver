package net.lax1dude.eaglercraft.backend.server.api;

import java.net.SocketAddress;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IBasePendingConnection extends IBaseConnection {

	int getMinecraftProtocol();

	@Nonnull
	SocketAddress getPlayerAddress();

	boolean isEaglerPlayer();

	@Nullable
	IEaglerPendingConnection asEaglerPlayer();

}
