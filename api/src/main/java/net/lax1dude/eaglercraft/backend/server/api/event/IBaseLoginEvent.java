package net.lax1dude.eaglercraft.backend.server.api.event;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerLoginConnection;

public interface IBaseLoginEvent<PlayerObject> extends IBaseServerEvent<PlayerObject> {

	IEaglerLoginConnection getLoginConnection();

}
