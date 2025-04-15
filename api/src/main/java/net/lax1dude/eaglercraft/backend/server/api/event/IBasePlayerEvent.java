package net.lax1dude.eaglercraft.backend.server.api.event;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;

public interface IBasePlayerEvent<PlayerObject> extends IBaseServerEvent<PlayerObject> {

	@Nonnull
	IEaglerPlayer<PlayerObject> getPlayer();

}
