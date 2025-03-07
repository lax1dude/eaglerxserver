package net.lax1dude.eaglercraft.backend.server.api.event;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerConnection;

public interface IEaglercraftWebSocketOpenEvent<PlayerObject> extends IBaseServerEvent<PlayerObject>, ICancellableEvent {

	IEaglerConnection getConnection();

}
