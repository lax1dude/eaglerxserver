package net.lax1dude.eaglercraft.backend.rpc.api.event;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;

public interface IBaseServerEvent<PlayerObject> {

	IEaglerXBackendRPC<PlayerObject> getServerAPI();

}
