package net.lax1dude.eaglercraft.backend.server.api.internal.factory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeManager;

public abstract class EaglerXServerAPIFactory implements IEaglerAPIFactory {

	public static final EaglerXServerAPIFactory INSTANCE;

	static {
		try {
			Class<?> clz = Class.forName("net.lax1dude.eaglercraft.backend.server.base.APIFactoryImpl");
			Method meth = clz.getMethod("createFactory");
			meth.setAccessible(true);
			INSTANCE = (EaglerXServerAPIFactory) meth.invoke(null);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new UnsupportedOperationException("Could not access the EaglerXServerAPI factory!", e);
		}
	}

	protected EaglerXServerAPIFactory() {
	}

	public abstract Class<?> getPlayerClass();

	public abstract IAttributeManager getGlobalAttributeManager();

	public abstract <T> IEaglerXServerAPI<T> createAPI(Class<T> playerClass);

}
