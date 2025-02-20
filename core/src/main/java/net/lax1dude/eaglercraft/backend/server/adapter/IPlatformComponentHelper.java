package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IPlatformComponentHelper {

	IPlatformComponentBuilder builder();

	String serializeLegacySection(Object component);

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

}
