package net.lax1dude.eaglercraft.backend.server.api.attribute;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.internal.factory.EaglerXServerAPIFactory;

public interface IAttributeManager {

	@Nonnull
	public static IAttributeManager instance() {
		return EaglerXServerAPIFactory.INSTANCE.getGlobalAttributeManager();
	}

	@Nonnull
	<T> IAttributeKey<T> initGlobalAttribute(@Nonnull String name, @Nonnull Class<T> type);

	@Nonnull
	<T> IAttributeKey<T> initPrivateAttribute(@Nonnull String name, @Nonnull Class<T> type);

	@Nonnull
	<T> IAttributeKey<T> initPrivateAttribute(@Nonnull Class<T> type);

}
