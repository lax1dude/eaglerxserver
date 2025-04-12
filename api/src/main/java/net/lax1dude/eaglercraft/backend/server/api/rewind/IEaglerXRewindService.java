package net.lax1dude.eaglercraft.backend.server.api.rewind;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;

public interface IEaglerXRewindService<PlayerObject> {

	IEaglerXServerAPI<PlayerObject> getServerAPI();

	boolean isEnabled();

	boolean isActive();

	void registerLegacyProtocol(IEaglerXRewindProtocol<PlayerObject, ?> protocolHandler);

	void unregisterLegacyProtocol(IEaglerXRewindProtocol<PlayerObject, ?> protocolHandler);

	boolean hasLegacyProtocol(int protocolVersion);

}
