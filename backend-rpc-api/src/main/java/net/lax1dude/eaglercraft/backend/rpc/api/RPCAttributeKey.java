package net.lax1dude.eaglercraft.backend.rpc.api;

import java.util.concurrent.ExecutionException;

import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public final class RPCAttributeKey<T> {

	private static final Cache<String, RPCAttributeKey<?>> globalAttrs = CacheBuilder.newBuilder().build();

	public static <T> RPCAttributeKey<T> createGlobal(String name, Class<T> type) {
		if(name == null) {
			throw new NullPointerException("name");
		}
		if(type == null) {
			throw new NullPointerException("type");
		}
		RPCAttributeKey<?> ret;
		try {
			ret = globalAttrs.get(name, () -> {
				return new RPCAttributeKey<>(type);
			});
		} catch (ExecutionException e) {
			Throwables.throwIfUnchecked(e.getCause());
			throw new RuntimeException(e.getCause());
		}
		if(ret.type != type) {
			throw new ClassCastException("Existing global attribute \"" + name + "\" registered type "
					+ ret.type.getName() + " does not match requested type " + type.getName());
		}
		return (RPCAttributeKey<T>) ret;
	}

	public static <T> RPCAttributeKey<T> createLocal(Class<T> type) {
		if(type == null) {
			throw new NullPointerException("type");
		}
		return new RPCAttributeKey<>(type);
	}

	private final Class<T> type;

	private RPCAttributeKey(Class<T> type) {
		this.type = type;
	}

	public Class<T> getType() {
		return type;
	}

}
