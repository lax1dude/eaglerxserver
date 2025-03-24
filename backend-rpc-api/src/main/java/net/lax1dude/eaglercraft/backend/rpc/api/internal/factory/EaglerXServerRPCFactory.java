package net.lax1dude.eaglercraft.backend.rpc.api.internal.factory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXServerRPC;

public abstract class EaglerXServerRPCFactory {

	public static final Factory INSTANCE;

	static {
		try {
			Class<?> clz = Class.forName("net.lax1dude.eaglercraft.backend.rpc.base.APIFactoryImpl");
			Method meth = clz.getDeclaredMethod("createFactory");
			meth.setAccessible(true);
			INSTANCE = (Factory) meth.invoke(null);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new UnsupportedOperationException("Could not access the EaglerXServerRPC factory!", e);
		}
	}

	protected EaglerXServerRPCFactory() {
	}

	public static abstract class Factory implements IEaglerRPCFactory {

		public abstract Set<Class<?>> getPlayerTypes();

		public abstract <T> IEaglerXServerRPC<T> getAPI(Class<T> playerClass);

		public abstract IEaglerXServerRPC<?> getDefaultAPI();

	}

}
