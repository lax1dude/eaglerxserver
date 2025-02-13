package net.lax1dude.eaglercraft.backend.server.api.attribute;

public interface IAttributeManager {

	<T> IAttributeKey<T> initGlobalAttribute(String name, Class<T> type);

	<T> IAttributeKey<T> initLocalAttribute(String name, Class<T> type);

	<T> IAttributeKey<T> initLocalAttribute(Class<T> type);

}
