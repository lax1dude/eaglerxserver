package net.lax1dude.eaglercraft.backend.rpc.api.event;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;

public interface IBaseServerEvent<PlayerObject> {

	@Nonnull
	IEaglerXBackendRPC<PlayerObject> getServerAPI();

}
