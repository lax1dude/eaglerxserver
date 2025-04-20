package net.lax1dude.eaglercraft.backend.server.api;

import javax.annotation.Nonnull;

public interface IComponentHelper {

	@Nonnull
	String serializeLegacyTextToLegacyJSON(@Nonnull String text) throws IllegalArgumentException;

	@Nonnull
	String convertJSONToLegacySection(@Nonnull String json) throws IllegalArgumentException;

	@Nonnull
	String convertJSONToPlainText(@Nonnull String json) throws IllegalArgumentException;

	@Nonnull
	String translateAlternateColorCodes(char altColorChar, @Nonnull String textToTranslate);

}
