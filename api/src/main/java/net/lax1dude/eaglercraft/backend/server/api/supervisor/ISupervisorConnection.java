package net.lax1dude.eaglercraft.backend.server.api.supervisor;

import java.net.SocketAddress;

import net.lax1dude.eaglercraft.backend.server.api.INettyChannel;

public interface ISupervisorConnection extends INettyChannel {

	SocketAddress getRemoteAddress();

	int getProtocolVersion();

	long getPing();

}
