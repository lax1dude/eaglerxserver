package net.lax1dude.eaglercraft.backend.server.api.supervisor;

import java.util.UUID;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.brand.IBrandResolver;
import net.lax1dude.eaglercraft.backend.server.api.collect.IntProcedure;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinResolver;

public interface ISupervisorResolver extends IBrandResolver, ISkinResolver {

	boolean isPlayerKnown(@Nonnull UUID playerUUID);

	int getCachedNodeId(@Nonnull UUID playerUUID);

	void resolvePlayerNodeId(@Nonnull UUID playerUUID, @Nonnull IntProcedure callback);

}
