package net.lax1dude.eaglercraft.eaglerxserver.base;

import net.lax1dude.eaglercraft.eaglerxserver.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.eaglerxserver.api.internal.factory.EaglerXServerAPIFactory;

class APIFactoryImpl extends EaglerXServerAPIFactory {

	static final APIFactoryImpl INSTANCE = new APIFactoryImpl();

	private Class<?> playerClass;
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

	@Override
	public IEaglerXServerAPI<?> createAPI(Class<?> playerClass) {
		IEaglerXServerAPI<?> handle = this.handle;
		if(handle == null) {
			throw new IllegalStateException("EaglerXServer has not been initialized yet!");
		}
		if(playerClass != this.playerClass) {
			throw new ClassCastException("Class \"" + playerClass.getName() + "\" cannot be cast to \"" + this.playerClass.getName() + "\"");
		}
		return handle;
	}

	<T> void initialize(Class<T> playerClass, IEaglerXServerAPI<T> handle) {
		this.playerClass = playerClass;
		this.handle = handle;
	}

	static EaglerXServerAPIFactory createFactory() {
		return INSTANCE;
	}

}
