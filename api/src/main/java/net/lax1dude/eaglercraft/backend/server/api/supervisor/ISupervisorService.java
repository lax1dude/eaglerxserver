package net.lax1dude.eaglercraft.backend.server.api.supervisor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;

public interface ISupervisorService<PlayerObject> {

	@Nonnull
	IEaglerXServerAPI<PlayerObject> getServerAPI();

	boolean isSupervisorEnabled();

	boolean isSupervisorConnected();

	@Nullable
	ISupervisorConnection getConnection();

	int getNodeId();

	int getPlayerTotal();

	int getPlayerMax();

	@Nonnull
	ISupervisorRPCHandler getRPCHandler();

	@Nonnull
	ISupervisorResolver getPlayerResolver();

	@Nonnull
	ISupervisorResolver getRemoteOnlyResolver();

}
