package net.lax1dude.eaglercraft.backend.rpc.api.event;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayerRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCHandle;

public interface IBasePlayerEvent<PlayerObject> extends IBaseServerEvent<PlayerObject> {

	IEaglerPlayer<PlayerObject> getPlayer();

	default IRPCHandle<IEaglerPlayerRPC<PlayerObject>> getHandle() {
		return getPlayer().getHandle();
	}

}
