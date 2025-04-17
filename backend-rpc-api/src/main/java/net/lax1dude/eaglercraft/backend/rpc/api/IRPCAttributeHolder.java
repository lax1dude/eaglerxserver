package net.lax1dude.eaglercraft.backend.rpc.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IRPCAttributeHolder {

	@Nullable
	<T> T get(@Nonnull RPCAttributeKey<T> key);

	<T> void set(@Nonnull RPCAttributeKey<T> key, @Nullable T value);

}
