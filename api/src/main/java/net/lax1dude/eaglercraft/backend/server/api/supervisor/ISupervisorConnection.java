package net.lax1dude.eaglercraft.backend.server.api.supervisor;

import java.net.SocketAddress;

public interface ISupervisorConnection {

	SocketAddress getRemoteAddress();

	int getProtocolVersion();

	long getPing();

	Unsafe unsafe();

	public interface Unsafe {

		void writePacket(Object packet);

	}

}
