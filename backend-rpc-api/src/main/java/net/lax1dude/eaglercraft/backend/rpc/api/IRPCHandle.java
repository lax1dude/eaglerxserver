package net.lax1dude.eaglercraft.backend.rpc.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IRPCHandle<T> {

	@Nullable
	T getIfOpen();

	@Nonnull
	IRPCFuture<T> openFuture();

}
