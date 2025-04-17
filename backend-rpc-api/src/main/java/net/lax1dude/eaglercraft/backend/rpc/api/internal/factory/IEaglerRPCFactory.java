package net.lax1dude.eaglercraft.backend.rpc.api.internal.factory;

import java.util.Set;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;

public interface IEaglerRPCFactory {

	@Nonnull
	Set<Class<?>> getPlayerTypes();

	@Nonnull
	<T> IEaglerXBackendRPC<T> getAPI(@Nonnull Class<T> playerClass);

	@Nonnull
	IEaglerXBackendRPC<?> getDefaultAPI();

}
