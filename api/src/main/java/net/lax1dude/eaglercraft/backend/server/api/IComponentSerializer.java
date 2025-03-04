package net.lax1dude.eaglercraft.backend.server.api;

public interface IComponentSerializer<ComponentType> extends IComponentHelper {

	String serializeLegacySection(ComponentType component);

	String serializePlainText(ComponentType component);

	/**
	 * Use for components that do not contain hover events
	 */
	String serializeGenericJSON(ComponentType component);

	/**
	 * Use for components that contain hover events, on 1.8 clients
	 */
	String serializeLegacyJSON(ComponentType component);

	/**
	 * Use for components that contain hover events, on modern clients
	 */
	String serializeModernJSON(ComponentType component);

	/**
	 * Use for components that do not contain hover events
	 */
	ComponentType parseGenericJSON(String json) throws IllegalArgumentException;

	/**
	 * Use for components that contain hover events, on 1.8 clients
	 */
	ComponentType parseLegacyJSON(String json) throws IllegalArgumentException;

	/**
	 * Use for components that contain hover events, on modern clients
	 */
	ComponentType parseModernJSON(String json) throws IllegalArgumentException;

}
