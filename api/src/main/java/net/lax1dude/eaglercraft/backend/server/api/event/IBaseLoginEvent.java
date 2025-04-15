package net.lax1dude.eaglercraft.backend.server.api.event;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerLoginConnection;

public interface IBaseLoginEvent<PlayerObject> extends IBaseServerEvent<PlayerObject> {

	@Nonnull
	IEaglerLoginConnection getLoginConnection();

}
