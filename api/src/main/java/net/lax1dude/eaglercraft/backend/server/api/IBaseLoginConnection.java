package net.lax1dude.eaglercraft.backend.server.api;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IBaseLoginConnection extends IBasePendingConnection {

	@Nonnull
	UUID getUniqueId();

	@Nonnull
	String getUsername();

	boolean isOnlineMode();

	@Nullable
	IEaglerLoginConnection asEaglerPlayer();

}
