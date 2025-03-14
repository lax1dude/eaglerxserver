package net.lax1dude.eaglercraft.backend.server.base;

import java.util.Collections;
import java.util.Set;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeManager;
import net.lax1dude.eaglercraft.backend.server.api.internal.factory.EaglerXServerAPIFactory;

class APIFactoryImpl extends EaglerXServerAPIFactory.Factory {

	static final APIFactoryImpl INSTANCE = new APIFactoryImpl();

	private Class<?> playerClass;
	private Set<Class<?>> playerClassSet;
	private final EaglerAttributeManager attributeManager = new EaglerAttributeManager();
	private IEaglerXServerAPI<?> handle;

	private APIFactoryImpl() {
	}

	@Override
	public Set<Class<?>> getPlayerTypes() {
		Set<Class<?>> classSet = this.playerClassSet;
		if(classSet == null) {
			throw new IllegalStateException("EaglerXServer has not been initialized yet!");
		}
		return classSet;
	}

	EaglerAttributeManager getEaglerAttribManager() {
		return attributeManager;
	}

	@Override
	public IAttributeManager getGlobalAttributeManager() {
		return attributeManager;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> IEaglerXServerAPI<T> getAPI(Class<T> playerClass) {
		IEaglerXServerAPI<?> handle = this.handle;
		if(handle == null) {
			throw new IllegalStateException("EaglerXServer has not been initialized yet!");
		}
		if(!playerClass.isAssignableFrom(this.playerClass)) {
			throw new ClassCastException("Class \"" + this.playerClass.getName() + "\" cannot be cast to \"" + playerClass.getName() + "\"");
		}
		return (IEaglerXServerAPI<T>) handle;
	}

	@Override
	public IEaglerXServerAPI<?> getDefaultAPI() {
		IEaglerXServerAPI<?> handle = this.handle;
		if(handle == null) {
			throw new IllegalStateException("EaglerXServer has not been initialized yet!");
		}
		return handle;
	}

	<T> void initialize(Class<T> playerClass, IEaglerXServerAPI<T> handle) {
		this.playerClass = playerClass;
		this.playerClassSet = Collections.singleton(playerClass);
		this.handle = handle;
	}

	static EaglerXServerAPIFactory.Factory createFactory() {
		return INSTANCE;
	}

}
