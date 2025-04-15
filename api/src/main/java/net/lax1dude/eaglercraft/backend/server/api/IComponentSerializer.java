package net.lax1dude.eaglercraft.backend.server.api;

import javax.annotation.Nonnull;

public interface IComponentSerializer<ComponentType> extends IComponentHelper {

	@Nonnull
	String serializeLegacySection(@Nonnull ComponentType component);

	@Nonnull
	String serializePlainText(@Nonnull ComponentType component);

	/**
	 * Use for components that do not contain hover events
	 */
	@Nonnull
	String serializeGenericJSON(@Nonnull ComponentType component);

	/**
	 * Use for components that contain hover events, on 1.8 clients
	 */
	@Nonnull
	String serializeLegacyJSON(@Nonnull ComponentType component);

	/**
	 * Use for components that contain hover events, on modern clients
	 */
	@Nonnull
	String serializeModernJSON(@Nonnull ComponentType component);

	/**
	 * Use for components that do not contain hover events
	 */
	@Nonnull
	ComponentType parseGenericJSON(@Nonnull String json) throws IllegalArgumentException;

	/**
	 * Use for components that contain hover events, on 1.8 clients
	 */
	@Nonnull
	ComponentType parseLegacyJSON(@Nonnull String json) throws IllegalArgumentException;

	/**
	 * Use for components that contain hover events, on modern clients
	 */
	@Nonnull
	ComponentType parseModernJSON(@Nonnull String json) throws IllegalArgumentException;

}
