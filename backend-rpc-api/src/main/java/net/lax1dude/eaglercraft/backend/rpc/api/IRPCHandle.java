package net.lax1dude.eaglercraft.backend.rpc.api;

public interface IRPCHandle<T> {

	T getHandleIfPresent();

	IRPCFuture<T> requestHandle();

}
