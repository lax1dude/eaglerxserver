package net.lax1dude.eaglercraft.backend.rpc.adapter;

public interface IPlatformComponentHelper {

	Class<?> getComponentType();

	Object createTextComponent(String text);

	/**
	 * Use for components that do not contain hover events
	 */
	String serializeGenericJSON(Object component);

	/**
	 * Use for components that contain hover events, on 1.8 clients
	 */
	String serializeLegacyJSON(Object component);

	/**
	 * Use for components that contain hover events, on modern clients
	 */
	String serializeModernJSON(Object component);

	/**
	 * Use for components that do not contain hover events
	 */
	Object parseGenericJSON(String json) throws IllegalArgumentException;

	/**
	 * Use for components that contain hover events, on 1.8 clients
	 */
	Object parseLegacyJSON(String json) throws IllegalArgumentException;

	/**
	 * Use for components that contain hover events, on modern clients
	 */
	Object parseModernJSON(String json) throws IllegalArgumentException;

}
