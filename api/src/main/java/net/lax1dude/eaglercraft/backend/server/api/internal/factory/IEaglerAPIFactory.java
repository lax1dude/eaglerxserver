package net.lax1dude.eaglercraft.backend.server.api.internal.factory;

import java.util.Set;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeManager;

public interface IEaglerAPIFactory {

	@Nonnull
	Set<Class<?>> getPlayerTypes();

	@Nonnull
	IAttributeManager getGlobalAttributeManager();

	@Nonnull
	<T> IEaglerXServerAPI<T> getAPI(@Nonnull Class<T> playerClass);

	@Nonnull
	IEaglerXServerAPI<?> getDefaultAPI();

}
