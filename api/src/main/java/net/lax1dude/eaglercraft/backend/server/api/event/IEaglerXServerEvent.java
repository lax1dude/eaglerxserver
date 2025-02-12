package net.lax1dude.eaglercraft.backend.server.api.event;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;

public interface IEaglerXServerEvent<PlayerObject> {

	IEaglerXServerAPI<PlayerObject> getServerAPI();

}
