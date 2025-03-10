package net.lax1dude.eaglercraft.backend.server.api.supervisor;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;

public interface ISupervisorService<PlayerObject> {

	IEaglerXServerAPI<PlayerObject> getServerAPI();

	boolean isSupervisorEnabled();

	boolean isSupervisorConnected();

	ISupervisorConnection getConnection();

	int getNodeId();

	int getPlayerTotal();

	int getPlayerMax();

	ISupervisorRPCHandler getRPCHandler();

	ISupervisorResolver getPlayerResolver();

	ISupervisorResolver getRemoteOnlyResolver();

}
