package net.lax1dude.eaglercraft.backend.server.api.rewind;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;

public interface IEaglerXRewindService<PlayerObject> {

	@Nonnull
	IEaglerXServerAPI<PlayerObject> getServerAPI();

	boolean isEnabled();

	boolean isActive();

	void registerLegacyProtocol(@Nonnull IEaglerXRewindProtocol<PlayerObject, ?> protocolHandler);

	void unregisterLegacyProtocol(@Nonnull IEaglerXRewindProtocol<PlayerObject, ?> protocolHandler);

	boolean hasLegacyProtocol(int protocolVersion);

}
