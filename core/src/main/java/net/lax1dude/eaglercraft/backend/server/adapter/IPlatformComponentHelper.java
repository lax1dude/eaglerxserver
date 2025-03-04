package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IPlatformComponentHelper {

	IPlatformComponentBuilder builder();

	Class<?> getComponentType();

	String serializeLegacySection(Object component);

	String serializePlainText(Object component);

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
