package net.lax1dude.eaglercraft.backend.server.api.attribute;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IAttributeHolder {

	@Nullable
	<T> T get(@Nonnull IAttributeKey<T> key);

	<T> void set(@Nonnull IAttributeKey<T> key, @Nullable T value);

}
