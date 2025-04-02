package net.lax1dude.eaglercraft.backend.rpc.api.event;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXServerRPC;

public interface IBaseServerEvent<PlayerObject> {

	IEaglerXServerRPC<PlayerObject> getServerAPI();

}
