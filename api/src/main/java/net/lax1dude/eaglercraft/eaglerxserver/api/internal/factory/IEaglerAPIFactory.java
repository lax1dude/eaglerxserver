package net.lax1dude.eaglercraft.eaglerxserver.api.internal.factory;

import net.lax1dude.eaglercraft.eaglerxserver.api.IEaglerXServerAPI;

public interface IEaglerAPIFactory<PlayerObject> {

	Class<PlayerObject> getPlayerClass();

	IEaglerXServerAPI<PlayerObject> createAPI();

}
