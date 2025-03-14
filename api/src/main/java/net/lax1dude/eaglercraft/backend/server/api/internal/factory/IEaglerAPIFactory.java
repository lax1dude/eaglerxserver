package net.lax1dude.eaglercraft.backend.server.api.internal.factory;

import java.util.Set;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeManager;

public interface IEaglerAPIFactory {

	Set<Class<?>> getPlayerTypes();

	IAttributeManager getGlobalAttributeManager();

	<T> IEaglerXServerAPI<T> getAPI(Class<T> playerClass);

	IEaglerXServerAPI<?> getDefaultAPI();

}
