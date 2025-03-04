package net.lax1dude.eaglercraft.backend.server.api;

public interface IComponentHelper {

	String convertJSONToLegacySection(String json) throws IllegalArgumentException;

	String convertJSONToPlainText(String json) throws IllegalArgumentException;

}
