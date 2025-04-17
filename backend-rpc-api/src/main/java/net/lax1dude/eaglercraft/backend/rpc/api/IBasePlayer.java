package net.lax1dude.eaglercraft.backend.rpc.api;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IBasePlayer<PlayerObject> extends IRPCAttributeHolder {

	@Nonnull
	IEaglerXBackendRPC<PlayerObject> getServerAPI();

	@Nonnull
	PlayerObject getPlayerObject();

	boolean isRPCReady();

	boolean isEaglerPlayer();

	@Nullable
	IEaglerPlayer<PlayerObject> asEaglerPlayer();

	@Nonnull
	UUID getUniqueId();

	@Nonnull
	String getUsername();

	@Nonnull
	IRPCHandle<? extends IBasePlayerRPC<PlayerObject>> getHandle();

}
