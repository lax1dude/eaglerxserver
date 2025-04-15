package net.lax1dude.eaglercraft.backend.server.api.event;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPendingConnection;

public interface IBaseHandshakeEvent<PlayerObject> extends IBaseServerEvent<PlayerObject> {

	@Nonnull
	IEaglerPendingConnection getPendingConnection();

}
