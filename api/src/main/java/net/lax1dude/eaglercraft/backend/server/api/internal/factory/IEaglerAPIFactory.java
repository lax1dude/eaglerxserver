package net.lax1dude.eaglercraft.backend.server.api.internal.factory;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeManager;

public interface IEaglerAPIFactory {

	Class<?> getPlayerClass();

	IAttributeManager getGlobalAttributeManager();

	<T> IEaglerXServerAPI<T> createAPI(Class<T> playerClass);

}
