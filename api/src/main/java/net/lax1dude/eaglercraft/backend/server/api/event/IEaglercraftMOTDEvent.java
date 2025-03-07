package net.lax1dude.eaglercraft.backend.server.api.event;

import net.lax1dude.eaglercraft.backend.server.api.query.IMOTDConnection;

public interface IEaglercraftMOTDEvent<PlayerObject> extends IBaseServerEvent<PlayerObject> {

	IMOTDConnection getMOTDConnection();

}
