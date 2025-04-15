package net.lax1dude.eaglercraft.backend.server.api;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IEaglerLoginConnection extends IBaseLoginConnection, IEaglerPendingConnection {

	@Nonnull
	byte[] getAuthUsername();

	boolean hasCapability(@Nonnull EnumCapabilitySpec capability);

	int getCapability(@Nonnull EnumCapabilityType capability);

	boolean hasExtendedCapability(@Nonnull UUID extendedCapability, int version);

	int getExtendedCapability(@Nonnull UUID extendedCapability);

	boolean isCookieSupported();

	boolean isCookieEnabled();

	@Nullable
	byte[] getCookieData();

}
