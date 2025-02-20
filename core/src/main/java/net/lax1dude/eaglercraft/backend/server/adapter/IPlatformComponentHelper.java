package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IPlatformComponentHelper {

	IPlatformComponentBuilder builder();

	Object parseLegacySection(String str);

	Object parseLegacyJSON(String str) throws IllegalArgumentException;

	Object parseModernJSON(String str) throws IllegalArgumentException;

	String serializeLegacySection(Object component);

	String serializeLegacyJSON(Object component);

	String serializeModernJSON(Object component);

}
