package net.lax1dude.eaglercraft.backend.server.base.supervisor.rpc;

import java.lang.reflect.Constructor;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import net.lax1dude.eaglercraft.backend.server.api.supervisor.data.ISupervisorData;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.data.SupervisorDataVoid;

class SupervisorDataType {

	private static final LoadingCache<Class<? extends ISupervisorData>, SupervisorDataType> dataTypeCache = CacheBuilder
			.newBuilder().weakKeys().weakValues()
			.build(new CacheLoader<Class<? extends ISupervisorData>, SupervisorDataType>() {
				@Override
				public SupervisorDataType load(Class<? extends ISupervisorData> var1) throws Exception {
					return new SupervisorDataType(var1);
				}
			});

	static final SupervisorDataType VOID_TYPE = new SupervisorDataType();

	protected final Class<? extends ISupervisorData> clazz;
	protected final Constructor<? extends ISupervisorData> ctor;

	private SupervisorDataType(Class<? extends ISupervisorData> clazz) {
		this.clazz = clazz;
		try {
			this.ctor = clazz.getConstructor();
		} catch (NoSuchMethodException | SecurityException e) {
			throw new IllegalArgumentException("Data class must define a default constructor with zero arguments!");
		}
	}

	private SupervisorDataType() {
		this.clazz = SupervisorDataVoid.class;
		this.ctor = null;
	}

	static SupervisorDataType provideType(Class<? extends ISupervisorData> clazz) {
		if(clazz == SupervisorDataVoid.class) {
			return VOID_TYPE;
		}else {
			try {
				return dataTypeCache.get(clazz);
			} catch (ExecutionException e) {
				if(e.getCause() instanceof RuntimeException ee) throw ee;
				throw new RuntimeException(e.getCause());
			}
		}
	}

}
