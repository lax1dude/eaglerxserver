package net.lax1dude.eaglercraft.backend.server.api.event;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;

public interface IBasePlayerEvent<PlayerObject> extends IBaseServerEvent<PlayerObject> {

	IEaglerPlayer<PlayerObject> getPlayer();

}
