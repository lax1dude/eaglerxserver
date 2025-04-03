package net.lax1dude.eaglercraft.backend.rpc.base;

import java.util.Collections;
import java.util.Set;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.internal.factory.EaglerXBackendRPCFactory;

class APIFactoryImpl extends EaglerXBackendRPCFactory.Factory {

	static final APIFactoryImpl INSTANCE = new APIFactoryImpl();

	private Class<?> playerClass;
	private Set<Class<?>> playerClassSet;
	private IEaglerXBackendRPC<?> handle;

	private APIFactoryImpl() {
	}

	@Override
	public Set<Class<?>> getPlayerTypes() {
		Set<Class<?>> classSet = this.playerClassSet;
		if(classSet == null) {
			throw new IllegalStateException("EaglerXBackendRPC has not been initialized yet!");
		}
		return classSet;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> IEaglerXBackendRPC<T> getAPI(Class<T> playerClass) {
		IEaglerXBackendRPC<?> handle = this.handle;
		if(handle == null) {
			throw new IllegalStateException("EaglerXBackendRPC has not been initialized yet!");
		}
		if(!playerClass.isAssignableFrom(this.playerClass)) {
			throw new ClassCastException("Class \"" + this.playerClass.getName() + "\" cannot be cast to \"" + playerClass.getName() + "\"");
		}
		return (IEaglerXBackendRPC<T>) handle;
	}

	@Override
	public IEaglerXBackendRPC<?> getDefaultAPI() {
		IEaglerXBackendRPC<?> handle = this.handle;
		if(handle == null) {
			throw new IllegalStateException("EaglerXBackendRPC has not been initialized yet!");
		}
		return handle;
	}

	<T> void initialize(Class<T> playerClass, IEaglerXBackendRPC<T> handle) {
		this.playerClass = playerClass;
		this.playerClassSet = Collections.singleton(playerClass);
		this.handle = handle;
	}

	static EaglerXBackendRPCFactory.Factory createFactory() {
		return INSTANCE;
	}

}
