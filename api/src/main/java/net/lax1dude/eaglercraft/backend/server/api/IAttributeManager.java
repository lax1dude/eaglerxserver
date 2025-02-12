package net.lax1dude.eaglercraft.backend.server.api;

public interface IAttributeManager<PlayerObject> {

	IEaglerXServerAPI<PlayerObject> getServerAPI();

	<T> IAttributeKey<T> getGlobalAttribute(String name, Class<T> type);

	<T> IAttributeKey<T> getLocalAttribute(String name, Class<T> type);

	<T> IAttributeKey<T> getLocalAttribute(Class<T> type);

}
