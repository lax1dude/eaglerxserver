package net.lax1dude.eaglercraft.backend.rpc.api.internal.factory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;

public abstract class EaglerXBackendRPCFactory {

	@Nonnull
	public static final Factory INSTANCE;

	static {
		// Dependency injection? Never heard of it
		try {
			Class<?> clz = Class.forName("net.lax1dude.eaglercraft.backend.rpc.base.APIFactoryImpl");
			Method meth = clz.getDeclaredMethod("createFactory");
			meth.setAccessible(true);
			INSTANCE = (Factory) meth.invoke(null);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new UnsupportedOperationException("Could not access the EaglerXBackendRPC factory!", e);
		}
	}

	protected EaglerXBackendRPCFactory() {
	}

	public static abstract class Factory implements IEaglerRPCFactory {

		@Nonnull
		@Override
		public abstract Set<Class<?>> getPlayerTypes();

		@Nonnull
		@Override
		public abstract <T> IEaglerXBackendRPC<T> getAPI(@Nonnull Class<T> playerClass);

		@Nonnull
		@Override
		public abstract IEaglerXBackendRPC<?> getDefaultAPI();

	}

}
