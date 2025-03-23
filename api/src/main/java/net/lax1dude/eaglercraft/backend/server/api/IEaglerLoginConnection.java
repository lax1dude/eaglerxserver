package net.lax1dude.eaglercraft.backend.server.api;

import java.util.UUID;

public interface IEaglerLoginConnection extends IBaseLoginConnection, IEaglerPendingConnection {

	boolean hasCapability(EnumCapabilitySpec capability);

	int getCapability(EnumCapabilityType capability);

	boolean hasExtendedCapability(UUID extendedCapability, int version);

	int getExtendedCapability(UUID extendedCapability);

	boolean isCookieSupported();

	boolean isCookieEnabled();

	byte[] getCookieData();

}
