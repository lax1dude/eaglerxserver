package net.lax1dude.eaglercraft.backend.server.api.event;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;

public interface IEaglerXPlayerEvent<PlayerObject> extends IEaglerXServerEvent<PlayerObject> {

	IEaglerPlayer<PlayerObject> getPlayer();

}
