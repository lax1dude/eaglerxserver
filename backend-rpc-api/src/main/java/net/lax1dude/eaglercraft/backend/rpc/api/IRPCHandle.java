package net.lax1dude.eaglercraft.backend.rpc.api;

public interface IRPCHandle<T> {

	T getIfOpen();

	IRPCFuture<T> openFuture();

}
