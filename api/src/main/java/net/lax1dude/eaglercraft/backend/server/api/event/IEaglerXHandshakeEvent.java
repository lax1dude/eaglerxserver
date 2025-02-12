package net.lax1dude.eaglercraft.backend.server.api.event;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPendingConnection;

public interface IEaglerXHandshakeEvent<PlayerObject> extends IEaglerXServerEvent<PlayerObject> {

	IEaglerPendingConnection getPendingConnection();

}
