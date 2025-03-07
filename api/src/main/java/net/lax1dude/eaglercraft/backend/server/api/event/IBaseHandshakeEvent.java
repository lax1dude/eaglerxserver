package net.lax1dude.eaglercraft.backend.server.api.event;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPendingConnection;

public interface IBaseHandshakeEvent<PlayerObject> extends IBaseServerEvent<PlayerObject> {

	IEaglerPendingConnection getPendingConnection();

}
