package net.lax1dude.eaglercraft.backend.rpc.base;

public interface IRPCFutureExpiring<V> extends IRPCFutureAbstract<V> {

	long expiresAt();

	void fireExceptionInternal(Throwable value);

}
