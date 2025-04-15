package net.lax1dude.eaglercraft.backend.server.api.supervisor;

import java.net.SocketAddress;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.INettyChannel;

public interface ISupervisorConnection extends INettyChannel {

	@Nonnull
	SocketAddress getRemoteAddress();

	int getProtocolVersion();

	int getNodeId();

	long getPing();

}
