package net.lax1dude.eaglercraft.backend.server.api;

public interface IAttributeKey<T> {

	boolean isGlobal();

	String getName();

	Class<T> getType();

}
