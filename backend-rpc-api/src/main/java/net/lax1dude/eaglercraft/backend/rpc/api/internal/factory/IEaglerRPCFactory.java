package net.lax1dude.eaglercraft.backend.rpc.api.internal.factory;

import java.util.Set;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;

public interface IEaglerRPCFactory {

	Set<Class<?>> getPlayerTypes();

	<T> IEaglerXBackendRPC<T> getAPI(Class<T> playerClass);

	IEaglerXBackendRPC<?> getDefaultAPI();

}
