package net.lax1dude.eaglercraft.backend.server.api.event;

import net.lax1dude.eaglercraft.backend.server.api.query.IMOTDConnection;

public interface IEaglercraftMOTDEvent<PlayerObject> extends IEaglerXServerEvent<PlayerObject> {

	IMOTDConnection getMOTDConnection();

}
