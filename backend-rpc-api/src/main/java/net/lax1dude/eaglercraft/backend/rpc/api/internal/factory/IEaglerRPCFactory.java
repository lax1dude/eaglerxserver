package net.lax1dude.eaglercraft.backend.rpc.api.internal.factory;

import java.util.Set;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXServerRPC;

public interface IEaglerRPCFactory {

	Set<Class<?>> getPlayerTypes();

	<T> IEaglerXServerRPC<T> getAPI(Class<T> playerClass);

	IEaglerXServerRPC<?> getDefaultAPI();

}
