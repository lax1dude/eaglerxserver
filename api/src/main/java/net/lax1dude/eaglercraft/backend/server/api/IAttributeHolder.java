package net.lax1dude.eaglercraft.backend.server.api;

public interface IAttributeHolder {

	<T> T get(IAttributeKey<T> key);

	<T> void set(IAttributeKey<T> key, T value);

}
