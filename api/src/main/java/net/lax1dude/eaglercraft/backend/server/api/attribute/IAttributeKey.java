package net.lax1dude.eaglercraft.backend.server.api.attribute;

import javax.annotation.Nonnull;

public interface IAttributeKey<T> {

	@Nonnull
	public static IAttributeManager factory() {
		return IAttributeManager.instance();
	}

	boolean isGlobal();

	@Nonnull
	String getName();

	@Nonnull
	Class<T> getType();

}
