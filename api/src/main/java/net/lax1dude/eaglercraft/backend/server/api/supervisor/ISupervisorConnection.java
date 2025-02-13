package net.lax1dude.eaglercraft.backend.server.api.supervisor;

import java.net.SocketAddress;

public interface ISupervisorConnection<PlayerObject> {

	ISupervisorService<PlayerObject> getSupervisorService();

	SocketAddress getRemoteAddress();

	int getProtocolVersion();

	long getPing();

	void writePacket(Object packet);

}
