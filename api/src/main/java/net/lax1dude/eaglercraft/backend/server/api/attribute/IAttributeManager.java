package net.lax1dude.eaglercraft.backend.server.api.attribute;

import net.lax1dude.eaglercraft.backend.server.api.internal.factory.EaglerXServerAPIFactory;

public interface IAttributeManager {

	public static IAttributeManager instance() {
		return EaglerXServerAPIFactory.INSTANCE.getGlobalAttributeManager();
	}

	<T> IAttributeKey<T> initGlobalAttribute(String name, Class<T> type);

	<T> IAttributeKey<T> initPrivateAttribute(String name, Class<T> type);

	<T> IAttributeKey<T> initPrivateAttribute(Class<T> type);

}
