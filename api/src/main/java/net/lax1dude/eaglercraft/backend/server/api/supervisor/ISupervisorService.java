package net.lax1dude.eaglercraft.backend.server.api.supervisor;

import java.net.SocketAddress;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.IBrandResolver;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinResolver;

public interface ISupervisorService<PlayerObject> extends IBrandResolver, ISkinResolver {

	IEaglerXServerAPI<PlayerObject> getServerAPI();

	boolean isSupervisorEnabled();

	boolean isSupervisorConnected();

	SocketAddress getSupervisorAddress();

	int getNodeId();

	int getPlayerTotal();

	int getPlayerMax();

	long getPingMS();

	ISupervisorRPCHandler<PlayerObject> getRPCHandler();

	boolean isPlayerKnown(UUID playerUUID);

	int getCachedNodeId(UUID playerUUID);

}
