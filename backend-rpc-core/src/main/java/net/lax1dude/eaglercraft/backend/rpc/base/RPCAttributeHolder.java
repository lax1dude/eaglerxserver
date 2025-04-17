package net.lax1dude.eaglercraft.backend.rpc.base;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.lax1dude.eaglercraft.backend.rpc.api.IRPCAttributeHolder;
import net.lax1dude.eaglercraft.backend.rpc.api.RPCAttributeKey;

public class RPCAttributeHolder implements IRPCAttributeHolder {

	private final ConcurrentMap<RPCAttributeKey<?>, Object> map = new ConcurrentHashMap<>();

	@Override
	public <T> void set(RPCAttributeKey<T> key, T value) {
		if(value != null) {
			map.put(key, value);
		}else {
			map.remove(key);
		}
	}

	@Override
	public <T> T get(RPCAttributeKey<T> key) {
		return (T) map.get(key);
	}

}
