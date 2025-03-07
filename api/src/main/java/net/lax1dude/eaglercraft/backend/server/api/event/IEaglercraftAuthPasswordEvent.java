package net.lax1dude.eaglercraft.backend.server.api.event;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.EnumPlatformType;
import net.lax1dude.eaglercraft.backend.server.api.UnsupportedOn;

public interface IEaglercraftAuthPasswordEvent<PlayerObject, ComponentObject> extends IBaseLoginEvent<PlayerObject> {

	public static enum EnumAuthResponse {
		ALLOW, DENY
	}

	byte[] getAuthUsername();

	byte[] getAuthSaltingData();

	boolean getCookiesEnabled();

	byte[] getCookieData();

	default String getCookieDataString() {
		byte[] ret = getCookieData();
		return ret != null ? new String(ret, StandardCharsets.UTF_8) : null;
	}

	byte[] getAuthPasswordDataResponse();

	String getProfileUsername();

	void setProfileUsername(String username);

	UUID getProfileUUID();

	@UnsupportedOn({EnumPlatformType.BUKKIT})
	void setProfileUUID(UUID uuid);

	IEaglercraftAuthCheckRequiredEvent.EnumAuthType getAuthType();

	String getAuthMessage();

	String getAuthRequestedServer();

	void setAuthRequestedServer(String server);

	EnumAuthResponse getAuthResponse();

	void setAuthResponse(EnumAuthResponse response);

	ComponentObject getKickMessage();

	void setKickMessage(ComponentObject kickMessage);

	void setKickMessage(String kickMessage);

	default void setLoginAllowed() {
		setAuthResponse(EnumAuthResponse.ALLOW);
	}

	default void setLoginDenied(ComponentObject kickMessage) {
		setKickMessage(kickMessage);
		setAuthResponse(EnumAuthResponse.DENY);
	}

	default void setLoginDenied(String kickMessage) {
		setKickMessage(kickMessage);
		setAuthResponse(EnumAuthResponse.DENY);
	}

	void applyTexturesProperty(String value, String signature);

	String getAppliedTexturesPropertyValue();

	String getAppliedTexturesPropertySignature();

	void setOverrideEaglerToVanillaSkins(boolean flag);

	boolean isOverrideEaglerToVanillaSkins();

}
