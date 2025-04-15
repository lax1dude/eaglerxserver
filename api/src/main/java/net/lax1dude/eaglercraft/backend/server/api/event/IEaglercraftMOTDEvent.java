package net.lax1dude.eaglercraft.backend.server.api.event;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.query.IMOTDConnection;

public interface IEaglercraftMOTDEvent<PlayerObject> extends IBaseServerEvent<PlayerObject> {

	@Nonnull
	IMOTDConnection getMOTDConnection();

}
