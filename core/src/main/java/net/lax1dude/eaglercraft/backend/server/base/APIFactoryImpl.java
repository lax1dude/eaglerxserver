package net.lax1dude.eaglercraft.backend.server.base;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeManager;
import net.lax1dude.eaglercraft.backend.server.api.internal.factory.EaglerXServerAPIFactory;

class APIFactoryImpl extends EaglerXServerAPIFactory {

	static final APIFactoryImpl INSTANCE = new APIFactoryImpl();

	private Class<?> playerClass;
	private final EaglerAttributeManager attributeManager = new EaglerAttributeManager();
	private IEaglerXServerAPI<?> handle;

	private APIFactoryImpl() {
	}

	@Override
	public Class<?> getPlayerClass() {
		Class<?> clazz = this.playerClass;
		if(clazz == null) {
			throw new IllegalStateException("EaglerXServer has not been initialized yet!");
		}
		return clazz;
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
	public <T> IEaglerXServerAPI<T> createAPI(Class<T> playerClass) {
		IEaglerXServerAPI<?> handle = this.handle;
		if(handle == null) {
			throw new IllegalStateException("EaglerXServer has not been initialized yet!");
		}
		if(playerClass != this.playerClass) {
			throw new ClassCastException("Class \"" + playerClass.getName() + "\" cannot be cast to \"" + this.playerClass.getName() + "\"");
		}
		return (IEaglerXServerAPI<T>) handle;
	}

	<T> void initialize(Class<T> playerClass, IEaglerXServerAPI<T> handle) {
		this.playerClass = playerClass;
		this.handle = handle;
	}

	static EaglerXServerAPIFactory createFactory() {
		return INSTANCE;
	}

}
