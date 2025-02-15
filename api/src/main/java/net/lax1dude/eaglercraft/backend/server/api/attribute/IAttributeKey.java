package net.lax1dude.eaglercraft.backend.server.api.attribute;

public interface IAttributeKey<T> {

	public static IAttributeManager factory() {
		return IAttributeManager.instance();
	}

	boolean isGlobal();

	String getName();

	Class<T> getType();

}
