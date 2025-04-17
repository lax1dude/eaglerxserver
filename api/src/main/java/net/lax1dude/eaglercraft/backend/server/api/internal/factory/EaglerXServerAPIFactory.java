package net.lax1dude.eaglercraft.backend.server.api.internal.factory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeManager;

public abstract class EaglerXServerAPIFactory {

	@Nonnull
	public static final Factory INSTANCE;

	static {
		// Dependency injection? Never heard of it
		try {
			Class<?> clz = Class.forName("net.lax1dude.eaglercraft.backend.server.base.APIFactoryImpl");
			Method meth = clz.getDeclaredMethod("createFactory");
			meth.setAccessible(true);
			INSTANCE = (Factory) meth.invoke(null);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new UnsupportedOperationException("Could not access the EaglerXServerAPI factory!", e);
		}
	}

	protected EaglerXServerAPIFactory() {
	}

	public static abstract class Factory implements IEaglerAPIFactory {

		@Nonnull
		@Override
		public abstract Set<Class<?>> getPlayerTypes();

		@Nonnull
		@Override
		public abstract IAttributeManager getGlobalAttributeManager();

		@Nonnull
		@Override
		public abstract <T> IEaglerXServerAPI<T> getAPI(@Nonnull Class<T> playerClass);

		@Nonnull
		@Override
		public abstract IEaglerXServerAPI<?> getDefaultAPI();

	}

}
