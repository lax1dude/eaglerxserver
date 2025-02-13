package net.lax1dude.eaglercraft.backend.server.api.brand;

import java.util.UUID;

public interface IBrandRegistration {

	UUID getBrandUUID();

	String getBrandDesc();

	boolean isVanillaMinecraft();

	boolean isVanillaEagler();

	boolean isLegacyClient();

	boolean isHackedClient();

}
