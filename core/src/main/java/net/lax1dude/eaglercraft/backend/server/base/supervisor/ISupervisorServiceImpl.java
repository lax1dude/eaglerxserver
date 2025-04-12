package net.lax1dude.eaglercraft.backend.server.base.supervisor;

import java.util.UUID;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.supervisor.ISupervisorService;
import net.lax1dude.eaglercraft.backend.server.base.supervisor.rpc.SupervisorRPCHandler;

public interface ISupervisorServiceImpl<PlayerObject> extends ISupervisorService<PlayerObject> {

	void handleEnable();

	void handleDisable();

	boolean shouldIgnoreUUID(UUID uuid);

	ISupervisorResolverImpl getRemoteOnlyResolver();

	SupervisorRPCHandler getRPCHandler();

	void acceptPlayer(UUID playerUUID, UUID brandUUID, int gameProtocol, int eaglerProtocol, String username,
			Consumer<EnumAcceptPlayer> callback);

	void dropOwnPlayer(UUID playerUUID);

	void notifySkinChange(UUID playerUUID, String serverName, boolean skin, boolean cape);

}
