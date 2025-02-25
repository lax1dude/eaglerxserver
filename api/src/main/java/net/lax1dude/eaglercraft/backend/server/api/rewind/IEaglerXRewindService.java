package net.lax1dude.eaglercraft.backend.server.api.rewind;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;

public interface IEaglerXRewindService<PlayerObject> {

	IEaglerXServerAPI<PlayerObject> getServerAPI();

	boolean isEnabled();

	boolean isActive();

	<T> void registerLegacyProtocol(IEaglerXRewindProtocol<PlayerObject, T> protocolHandler);

	<T> void unregisterLegacyProtocol(IEaglerXRewindProtocol<PlayerObject, T> protocolHandler);

	boolean hasLegacyProtocol(int protocolVersion);

}
