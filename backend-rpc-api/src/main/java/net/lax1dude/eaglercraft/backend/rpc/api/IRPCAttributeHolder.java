package net.lax1dude.eaglercraft.backend.rpc.api;

public interface IRPCAttributeHolder {

	<T> void set(RPCAttributeKey<T> key, T value);

	<T> T get(RPCAttributeKey<T> key);

}
