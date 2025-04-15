package net.lax1dude.eaglercraft.backend.server.api.brand;

import java.util.UUID;

import javax.annotation.Nonnull;

public interface IBrandRegistration {

	@Nonnull
	UUID getBrandUUID();

	@Nonnull
	String getBrandDesc();

	boolean isVanillaMinecraft();

	boolean isVanillaEagler();

	boolean isLegacyClient();

	boolean isHackedClient();

}
