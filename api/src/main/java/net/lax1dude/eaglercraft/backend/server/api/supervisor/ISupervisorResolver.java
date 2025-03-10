package net.lax1dude.eaglercraft.backend.server.api.supervisor;

import java.util.UUID;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.brand.IBrandResolver;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinResolver;

public interface ISupervisorResolver extends IBrandResolver, ISkinResolver {

	boolean isPlayerKnown(UUID playerUUID);

	int getCachedNodeId(UUID playerUUID);

	void resolvePlayerNodeId(UUID playerUUID, Consumer<Integer> callback);

}
