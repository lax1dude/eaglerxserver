package net.lax1dude.eaglercraft.backend.server.api.internal.factory;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;

public interface IEaglerAPIFactory {

	Class<?> getPlayerClass();

	<T> IEaglerXServerAPI<T> createAPI(Class<T> playerClass);

}
