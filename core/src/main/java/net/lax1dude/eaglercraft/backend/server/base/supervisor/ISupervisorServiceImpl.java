package net.lax1dude.eaglercraft.backend.server.base.supervisor;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.supervisor.ISupervisorService;

public interface ISupervisorServiceImpl<PlayerObject> extends ISupervisorService<PlayerObject> {

	void handleEnable();

	void handleDisable();

	boolean shouldIgnoreUUID(UUID uuid);

}
