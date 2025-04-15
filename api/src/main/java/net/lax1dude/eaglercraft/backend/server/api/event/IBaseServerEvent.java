package net.lax1dude.eaglercraft.backend.server.api.event;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;

public interface IBaseServerEvent<PlayerObject> {

	@Nonnull
	IEaglerXServerAPI<PlayerObject> getServerAPI();

}
